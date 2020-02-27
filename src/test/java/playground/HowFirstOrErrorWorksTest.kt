package playground

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

        testObserver.assertValues(5 to 5, 1 to 1, 2 to 2, 3 to 3)
    }

    @Test
    fun replay_subject_keeps_a_buffer() {
        val replaySubject = ReplaySubject.create<Int>()
        val testObserver = replaySubject.switchMapSingle { emission ->
            replaySubject.firstOrError().map { emission to it }
        }.test()

        replaySubject.emitEvents()

        testObserver.assertValues(5 to 5, 1 to 5, 2 to 5, 3 to 5)
    }

    @Test
    fun observable_create_keeps_intermediate_states() {
        val observable = Observable.create<Int> { emitter ->
            emitter.emitEvents()
        }

        val testObserver = observable.switchMapSingle { emission ->
            observable.firstOrError().map { emission to it }
        }.test()

        testObserver.assertValueAt(19999, 19999 to 0)
    }

    private fun ObservableEmitter<Int>.emitEvents() {
        for (i in 0 until 20000) onNext(i)
        onComplete()
    }

    private fun Subject<Int>.emitEvents() {
        onNext(5)
        onNext(1)
        onNext(2)
        onNext(3)
        onComplete()
    }
}
