package operators

import com.nhaarman.mockitokotlin2.mock
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class DistinctUntilChangedExtensionsKtTest {

    @Test
    fun distinctUntilChangedOfNormalSource() {
        val w = mock<Observer<String>> {}
        val src =
            Observable.just("a", "b", "c", "c", "c", "b", "b", "a", "e")
        src.distinctUntilChanged2().subscribe(w)

        val inOrder = Mockito.inOrder(w)
        inOrder.verify<Observer<String>>(w, Mockito.times(1)).onNext("a")
        inOrder.verify<Observer<String>>(w, Mockito.times(1)).onNext("b")
        inOrder.verify<Observer<String>>(w, Mockito.times(1)).onNext("c")
        inOrder.verify<Observer<String>>(w, Mockito.times(1)).onNext("b")
        inOrder.verify<Observer<String>>(w, Mockito.times(1)).onNext("a")
        inOrder.verify<Observer<String>>(w, Mockito.times(1)).onNext("e")
        inOrder.verify<Observer<String>>(w, Mockito.times(1)).onComplete()
        inOrder.verify<Observer<String>>(w, Mockito.never())
            .onNext(ArgumentMatchers.anyString())
        Mockito.verify<Observer<String>>(w, Mockito.never()).onError(
            ArgumentMatchers.any(
                Throwable::class.java
            )
        )
    }
}