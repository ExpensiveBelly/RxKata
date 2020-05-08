package it.droidcon.testingdaggerrxjava.playground.disposal

import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.junit.Test


class DoOnSubscribeOrderInTheStream {

    private interface Verifier {
        fun observableDoOnSubscribe()
        fun singleDoOnSubscribe()
        fun completableDoOnSubscribe()
    }

    private val completableFun = { verifier: Verifier ->
        Observable.just(1)
            .flatMapSingle {
                Single.just("Item")
                    .doOnSubscribe { verifier.singleDoOnSubscribe() }
            }
            .flatMapCompletable {
                Completable.fromAction { println("fromAction Completable") }
                    .doOnSubscribe { verifier.completableDoOnSubscribe() }
            }
            .doOnSubscribe { verifier.observableDoOnSubscribe() }
    }


    @Test
    fun `should call doOnsubscribe in order`() {
        val verifier = mock<Verifier> {}

        completableFun(verifier).blockingAwait()

        inOrder(verifier) {
            verify(verifier).observableDoOnSubscribe()
            verify(verifier).singleDoOnSubscribe()
            verify(verifier).completableDoOnSubscribe()

        }
    }
}