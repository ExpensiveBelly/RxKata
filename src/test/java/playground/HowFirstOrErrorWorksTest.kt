package playground

import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject
import org.junit.Test

class HowFirstOrErrorWorksTest {

    @Test
    fun publish_subject_throws_an_exception() {
        val publishSubject = PublishSubject.create<Int>()
        val testObserver = publishSubject.switchMapSingle { emission ->
            publishSubject.firstOrError()
        }.test()

        publishSubject.emitEvents()

        testObserver.assertError(NoSuchElementException::class.java)
    }

    @Test
    fun behavior_subject_only_emits_the_last_item() {
        val behaviorSubject = BehaviorSubject.create<Int>()
        val testObserver = behaviorSubject.switchMapSingle { emission ->
            behaviorSubject.firstOrError().map { emission to it }
        }.test()

        behaviorSubject.emitEvents()

        testObserver.assertValues(0 to 0, 1 to 1, 2 to 2, 3 to 3)
    }

    @Test
    fun replay_subject_keeps_a_buffer() {
        val replaySubject = ReplaySubject.create<Int>()
        val testObserver = replaySubject.switchMapSingle { emission ->
            replaySubject.firstOrError().map { emission to it }
        }.test()

        replaySubject.emitEvents()

        testObserver.assertValues(0 to 0, 1 to 0, 2 to 0, 3 to 0)
    }

    @Test
    fun observable_create_keeps_resubscribing_and_it_looks_like_it_keeps_states_but_it_doesnt() {
        val observable = Observable.create<Int> { emitter ->
            emitter.emitEvents()
        }

        val testObserver = observable.switchMapSingle { emission ->
            observable.firstOrError().map { emission to it }
        }.test()

        testObserver.assertValueAt(1999, 1999 to 0)
    }

    @Test
    fun observable_create_behaves_like_a_publish_subject_it_does_not_keep_the_intermediate_emissions_when_hot() {
        val observable = Observable.create<Int> { emitter ->
            emitter.emitEvents()
        }.share()

        val testObserver = observable.switchMapSingle { emission ->
            observable.firstOrError().map { emission to it }
        }.test()

        testObserver.assertError(NoSuchElementException::class.java)
    }

    private interface Verifier {
        fun doOnDispose()
        fun doOnSuccess()
    }

    @Test
    fun firstOrError_completes_downstream_and_dispose_upstream() {
        val publishSubject = PublishSubject.create<Int>()
        val verifier = mock<Verifier> {}

        val testObserver = publishSubject
            .doOnDispose { verifier.doOnDispose() }
            .firstOrError()
            .doOnSuccess { verifier.doOnSuccess() }
            .test()

        publishSubject.onNext(1)

        testObserver.assertComplete()
        inOrder(verifier) {
            verify(verifier).doOnDispose()
            verify(verifier).doOnSuccess()
        }
    }

    private fun ObservableEmitter<Int>.emitEvents() {
        for (i in 0 until 2000) onNext(i)
        onComplete()
    }

    private fun Subject<Int>.emitEvents() {
        onNext(0)
        onNext(1)
        onNext(2)
        onNext(3)
        onComplete()
    }
}
