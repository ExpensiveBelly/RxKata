package playground.maybe

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Test
import java.util.concurrent.TimeUnit

class TakeUntilAndRepeatTest {

    private val singleOrError = Observable.just(true)
        .filter(true::equals)
        .repeat()
        .singleOrError()

    @Test
    fun `should throw IllegalArgumentException because the sequence contains more than one element due to the repeat`() {
        singleOrError.test().assertError(IllegalArgumentException::class.java)
    }

    val subject = PublishSubject.create<Unit>()
    val subjectStop = PublishSubject.create<Unit>()

    private interface Verifier {
        fun doOnSubscribe()
        fun doOnError()
        fun doOnNext(long: Long)
        fun subjectStopNext()
    }

    private val observableFun = { scheduler: Scheduler, verifier: Verifier ->
        subject
            .flatMap {
                Observable.interval(500, TimeUnit.MILLISECONDS, scheduler)
                    .takeUntil(subjectStop)
                    .repeat()
            }
    }


    private val observableFunCompletable = { scheduler: Scheduler, scheduler2: Scheduler, verifier: Verifier ->
        subject
            .flatMap {
                Observable.interval(500, TimeUnit.MILLISECONDS, scheduler)
                    .doOnNext { verifier.doOnNext(it) }
                    .takeUntil(subjectStop.doOnNext { verifier.subjectStopNext() })
                    .repeat()
                    .flatMapSingle {
                        Single.fromCallable { "Item" }
                            .delay(1500, TimeUnit.MILLISECONDS, scheduler2)
                            .doOnSubscribe { verifier.doOnSubscribe() }
                            .map { throw IllegalStateException() }
                            .doOnError { verifier.doOnError() }
                    }
            }
    }

    @Test
    fun `should reset the counter in the interval operator when the subjectStop emits`() {
        val testScheduler = TestScheduler()
        val verifier = mock<Verifier> {}
        val testObserver = observableFun(testScheduler, verifier).test()

        subject.onNext(Unit)

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        testObserver.assertValue(0)

        subjectStop.onNext(Unit)

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        testObserver.assertValues(0, 0) //Re-subscription because of the `repeat`

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        testObserver.assertValues(0, 0, 1)
    }

    @Test
    fun `repeat should not stop the downstream from throwing an error`() {
        val testScheduler = TestScheduler()
        val testScheduler2 = TestScheduler()
        val verifier = mock<Verifier> {}
        val testObserver = observableFunCompletable(testScheduler, testScheduler2, verifier).test()

        subject.onNext(Unit)

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        verify(verifier).doOnNext(0)
        verify(verifier).doOnSubscribe()
        reset(verifier)

        subjectStop.onNext(Unit)
        verify(verifier).subjectStopNext()

        testScheduler2.advanceTimeBy(1500, TimeUnit.MILLISECONDS)
        verify(verifier).doOnError()
    }
}