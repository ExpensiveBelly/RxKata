package playground

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

class ShowErrorAfterThreeRetriesTest {
    private val retryCallbacks = AtomicInteger(0)
    private val retryingCallback: (String) -> Unit = { retryCallbacks.incrementAndGet() }
    private val retryScheduler = TestScheduler()
    private val mainScheduler = TestScheduler()

    val subject = PublishSubject.create<Result<String>>()
    private val observable = subject.flatMapSingle {
        it.getOrNull()?.let { Single.just(it) }
            ?: Single.error(it.exceptionOrNull()!!)
    }

    private val observableWithRetry = observable
        .retryReset(3) { it is IOException }

    fun <T> Observable<T>.retryReset(count: Int, predicate: (Throwable) -> Boolean) =
        Single.fromCallable { AtomicInteger(0) }
            .flatMapObservable { counter ->
                retryWhen { errors ->
                    errors.flatMapSingle {
                        if (counter.getAndIncrement() < count && predicate(it)) Single.just(0)
                        else Single.error(it)
                    }.observeOn(mainScheduler)
                        .doOnNext { retryingCallback("Retrying... $counter ${Thread.currentThread().name}") }
                        .observeOn(retryScheduler)
                }.doOnNext { counter.set(0) }
            }

    fun <T> Observable<T>.retryReset(handler: (Observable<Throwable>) -> ObservableSource<*>): Observable<T> =
        Single.fromCallable { PublishSubject.create<Unit>() }
            .flatMapObservable { signal ->
                this.doOnNext { signal.onNext(Unit) }
                    .retryWhen {
                        Observable.defer { handler(it) }
                            .takeUntil(signal)
                            .repeat()
                    }
            }

    @Test
    fun behavior1() {
        val error = IllegalStateException()
        val observer = observableWithRetry.test()
        subject.onNext(Result.failure(error))
        mainScheduler.triggerActions()
        retryScheduler.triggerActions()
        observer.assertError(error)
        assertEquals(0, retryCallbacks.get())
        assertFalse(subject.hasObservers())
    }

    @Test
    fun behavior2() {
        val error = IOException()
        val observer = observableWithRetry.test()
        repeat(3) {
            subject.onNext(Result.failure(error))
            mainScheduler.triggerActions()
            assertEquals(it + 1, retryCallbacks.get())
            retryScheduler.triggerActions()
            observer.assertNoErrors()
        }
        subject.onNext(Result.failure(error))
        mainScheduler.triggerActions()
        assertEquals(3, retryCallbacks.get())
        retryScheduler.triggerActions()
        assertFalse(subject.hasObservers())
        observer.assertError(error)
    }

    @Test
    fun behavior3() {
        val observer = observableWithRetry.test()
        subject.onNext(Result.success("A1"))
        observer.assertValue("A1")
    }

    @Test
    fun retriesReset() {
        val error = IOException()
        val observer = observableWithRetry.test()
        repeat(2) {
            repeat(3) {
                subject.onNext(Result.failure(error))
                mainScheduler.triggerActions()
                assertEquals(it + 1, retryCallbacks.get())
                retryScheduler.triggerActions()
                observer.assertNoErrors()
            }
            subject.onNext(Result.success("1"))
            retryCallbacks.compareAndSet(3, 0)
        }
    }
}