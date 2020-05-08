package it.droidcon.testingdaggerrxjava.playground.empty

import arrow.core.extensions.list.functorFilter.filter
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test


class EmptyListSituation {

    @Test
    fun `should complete straight away if list is empty`() {
        Completable.merge(emptyList()).test().assertComplete()
    }

    private interface Verifier {
        fun insideMerge()
        fun insideAndThen()
        fun insidedoOnComplete()
    }

    @Test
    fun `should not invoke the function inside the Completable merge because the list is empty`() {
        val verifier = mock<Verifier> {}

        Completable.merge(listOf(1, 2, 3).filter { it > 3 }.map {
            verifier.insideMerge()
            Single.just(it).ignoreElement()
        }).andThen(Single.just("h").doOnSuccess { verifier.insideAndThen() })
            .ignoreElement()
            .doOnComplete { verifier.insidedoOnComplete() }
            .blockingAwait()

        verify(verifier, never()).insideMerge()
        inOrder(verifier) {
            verify(verifier).insideAndThen()
            verify(verifier).insidedoOnComplete()
        }
    }
}