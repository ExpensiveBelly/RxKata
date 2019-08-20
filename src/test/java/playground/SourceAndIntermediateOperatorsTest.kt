package playground

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.junit.Test


class SourceAndIntermediateOperatorsTest {

    private val subject = PublishSubject.create<Unit>()

    /**
     * Rx Marbles: firstOrError
     * https://www.google.com/search?q=firstOrError+vs+singleorerror&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjZqeKVlqvjAhVMZMAKHWY2DrEQ_AUIESgC&biw=1745&bih=789#imgrc=u8WJ7ke-CPl8bM:
     */

    @Test
    fun first_or_error_completes_parent_observable() {
        subject.firstOrError().test().assertSubscribed().also { subject.onNext(Unit) }.assertComplete()
    }

    @Test
    fun flatmap_does_not_complete_parent_observable() {
        subject.flatMap { Observable.just(Unit) }.test().assertSubscribed().also { subject.onNext(Unit) }.assertNotComplete()
    }

    @Test
    fun flatmap_completable_does_not_complete_parent_observable() {
        subject.flatMapCompletable { Completable.complete() }.test().assertSubscribed().also { subject.onNext(Unit) }.assertNotComplete()
    }

    @Test
    fun firstelement_completes_parent_observable() {
        subject.firstElement().test().assertSubscribed().also { subject.onNext(Unit) }.assertComplete()
    }

    @Test
    fun filter_does_not_complete_parent_observable() {
        subject.filter { it != Unit }.test().assertSubscribed().also { subject.onNext(Unit) }.assertNotComplete()
    }

    /**
     * Rx Marbles: singleOrError
     * https://www.google.com/search?q=firstOrError+vs+singleorerror&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjZqeKVlqvjAhVMZMAKHWY2DrEQ_AUIESgC&biw=1745&bih=789#imgrc=zhPScB2WL-0vGM:
     */

    @Test
    fun singleOrError_does_not_complete_parent_observable() {
        subject.singleOrError().test().assertSubscribed().also { subject.onNext(Unit) }.assertNotComplete()
    }

    @Test
    fun singleOrError_will_error_the_parent_observable() {
        subject.singleOrError().test().assertSubscribed().also {
            subject.onNext(Unit)
            subject.onNext(Unit)
        }.assertError(IllegalArgumentException::class.java)
    }

    @Test
    fun flatmap_completable_will_error_parent_observable() {
        subject.flatMapCompletable { Completable.error(RuntimeException()) }.test().also { subject.onNext(Unit) }.assertError(RuntimeException::class.java)
    }

    @Test
    fun subject_errors_when_observable_empty_concat_with_error_inside_flatmap() {
        subject.flatMap { Observable.empty<Unit>().concatWith(Observable.error(IllegalStateException())) }.test().also { subject.onNext(Unit) }.assertError(IllegalStateException::class.java)
    }

    @Test
    fun subject_does_not_complete_when_observable_empty_inside_flatmap() {
        subject.flatMap { Observable.empty<Unit>() }.concatWith(Observable.error(IllegalStateException())).test().also { subject.onNext(Unit) }.assertNoValues().assertNotComplete().assertNoErrors()
    }

    @Test
    fun first_or_error_errors_because_single_just_completes() {
        subject.firstOrError().flatMap { Single.just(Unit) }.concatWith(Single.error(IllegalStateException())).test().also {
            subject.onNext(Unit)
            subject.onComplete()
        }.assertError(IllegalStateException::class.java)
    }

    @Test
    fun observable_onExceptionResumeNext_not_passing_the_exception() {
        Observable.error<Throwable>(IllegalStateException()).onExceptionResumeNext { Observable.error<Throwable>(IllegalStateException()) }.test().assertNoErrors()
    }
}