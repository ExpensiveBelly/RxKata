package playground.disposal

import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Test

class DisposalExerciseTest {

    private interface RxLifecycle {
        fun doFinally()
        fun doOnDispose()
        fun doOnSubscribe()
        fun doOnComplete()
        fun unsubscribeOn()
    }

    /*
    Explanation: subscribe disconnects the upstream Disposable when it receives a terminal event thus you can't dispose an already completed sequence
    https://github.com/ReactiveX/RxJava/issues/5264
     */

    @Test
    fun subscribe_dispose() {
        val verifier = mock<RxLifecycle>()

        Observable.fromCallable { "2" }
            .doOnSubscribe { verifier.doOnSubscribe() }
            .doOnDispose { verifier.doOnDispose() }
            .subscribe()
            .dispose()

        verify(verifier).doOnSubscribe()
        verify(verifier, never()).doOnDispose()
    }

    //What is the fix?
    @Test
    fun subscribe_dispose_finally() {
        val verifier = mock<RxLifecycle>()

        Observable.fromCallable { "2" }
            .doOnSubscribe { verifier.doOnSubscribe() }
            .doFinally { verifier.doFinally() }
            .subscribe()
            .dispose()

        inOrder(verifier) {
            verify(verifier).doOnSubscribe()
            verify(verifier).doFinally()
        }
    }

    @Test
    fun dofinally_thread() {
        val verifier = mock<RxLifecycle>()

        Observable.just(1)
            .flatMapCompletable { Completable.complete() }
            .doFinally { verifier.doFinally() }
            .doOnComplete { verifier.doOnComplete() }
            .unsubscribeOn(Schedulers.from {
                verifier.unsubscribeOn()
                it.run()
            })
            .subscribe()

        inOrder(verifier) {
            verifier.doOnComplete()
            verifier.unsubscribeOn()
            verifier.doFinally()
        }
    }
}