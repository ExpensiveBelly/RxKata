package playground.curiosities

import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue


class CancelableVsDisposableTest {

    @Test
    fun cancelable_should_dispose_the_stream() {
        val testScheduler = TestScheduler()
        var innerDisposable = Disposables.empty()
        val testObserver = Observable.create<Int> { emitter ->
            innerDisposable = Observable.interval(0, 100, TimeUnit.MILLISECONDS, testScheduler)
                    .doOnNext { emitter.onNext(it.toInt()) }.subscribe()
            emitter.setCancellable { innerDisposable.dispose() }
        }.test()

        testObserver.assertSubscribed()
        testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)
        testObserver.assertValues(0, 1)
        testObserver.dispose()

        assertTrue { innerDisposable.isDisposed }
    }

    @Test
    fun disposable_should_dispose_the_stream() {
        val testScheduler = TestScheduler()
        var innerDisposable = Disposables.empty()
        val testObserver = Observable.create<Int> { emitter ->
            innerDisposable = Observable.interval(0, 100, TimeUnit.MILLISECONDS, testScheduler)
                    .doOnNext { emitter.onNext(it.toInt()) }.subscribe()
            emitter.setDisposable(Disposables.fromAction { innerDisposable.dispose() })
        }.test()

        testObserver.assertSubscribed()
        testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)
        testObserver.assertValues(0, 1)
        testObserver.dispose()

        assertTrue { innerDisposable.isDisposed }
    }
}