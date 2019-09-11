package playground.curiosities

import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class SingleCacheValuesTest {

    private fun <T> Single<T>.cacheValues(msTillDisconnect: Long = 0L) = toObservable().replay(1).refCount(msTillDisconnect, TimeUnit.MILLISECONDS).firstOrError()

    @Test
    fun should_cache_value_when_more_subscribers_subscribe() {
        val testScheduler = TestScheduler()
        val text = "Hello"
        val singleCached = Single.defer { Single.just(text).delay(100, TimeUnit.MILLISECONDS, testScheduler) }.cacheValues(1)

        singleCached.test().assertSubscribed().assertNoValues().also { testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS) }.assertValue(text)
        singleCached.test().assertValue(text)
    }

    @Test
    fun should_reset_the_cached_value_if_no_subscribers_subscribed() {
        val text1 = "Hello"
        val text2 = "World"
        var firstTime = true
        val singleCached = Single.defer { Single.just(if (firstTime) text1 else text2).also { firstTime = false } }.cacheValues()

        runBlocking {
            singleCached.test().assertSubscribed().assertValue(text1)
            delay(100)
            singleCached.test().assertValue(text2)
        }
    }

    private fun <T> Single<T>.cacheAtomicReference(): Single<T> {
        val reference = AtomicReference<T>()
        val referenceShared = doOnSuccess { reference.set(it) }.toObservable().replay(1).refCount().firstOrError()
        return Single.defer {
            val value = reference.get()
            if (value != null) Single.just(value)
            else referenceShared
        }
    }

    @Test
    fun should_share_atomic_reference_value_when_multiple_subscribers_subscribe() {
        val testScheduler = TestScheduler()
        val text = "Hello"
        val singleCached = Single.defer { Single.just(text).delay(100, TimeUnit.MILLISECONDS, testScheduler) }.cacheAtomicReference()

        val subscriber1 = singleCached.test()
        testScheduler.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        subscriber1.assertNoValues()
        val subscriber2 = singleCached.test()
        testScheduler.advanceTimeBy(50, TimeUnit.MILLISECONDS)
        subscriber1.assertValue(text)
        subscriber2.assertValue(text)
    }

    @Test
    fun should_not_persist_errors_and_try_again_when_using_cache_atomic_reference() {
        val text = "Hello"
        var firstTime = true
        val singleCachedError = Single.defer {
            if (firstTime) {
                Single.error<Throwable>(IllegalStateException()).doOnError { firstTime = false }
            } else {
                Single.just(text)
            }
        }.cacheAtomicReference()

        singleCachedError.test().assertError(IllegalStateException::class.java)
        singleCachedError.test().assertValue(text)
        singleCachedError.test().assertValue(text)
    }

    @Test
    fun should_cache_atomic_reference_even_if_unsubscribed_and_share_the_same_value() {
        val text = "Hello"
        val singleCached = Single.defer { Single.just(text) }.cacheAtomicReference()

        val disposable = singleCached.subscribe()

        disposable.dispose()

        singleCached.test().assertValue(text)
    }

    /**
     * What's the difference between `cacheAtomicReference` and `cache` operator?
     */

    @Test
    fun `cache_should_re-throw_the_error_whereas_cacheAtomicReference_should_not`() {
        val text = "Hello"
        var firstTimeCacheAtomicReference = true
        val singleCachedAtomicReference = Single.defer {
            if (firstTimeCacheAtomicReference) {
                Single.error<Throwable>(IllegalStateException()).doOnError { firstTimeCacheAtomicReference = false }
            } else {
                Single.just(text)
            }
        }.cacheAtomicReference()

        var firstTimeCache = true
        val singleCached = Single.defer {
            if (firstTimeCache) {
                Single.error<Throwable>(IllegalStateException()).doOnError { firstTimeCache = false }
            } else {
                Single.just(text)
            }
        }.cache()

        singleCachedAtomicReference.test().assertError(IllegalStateException::class.java)
        // Subsequent subscription triggers the source again
        singleCachedAtomicReference.test().assertValue(text)

        singleCached.test().assertError(IllegalStateException::class.java)
        // Subsequent subscription throws error again
        singleCached.test().assertError(IllegalStateException::class.java)
    }
}