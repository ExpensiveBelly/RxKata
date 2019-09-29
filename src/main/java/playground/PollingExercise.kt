package playground

import com.github.davidmoten.rx2.RetryWhen
import com.nytimes.android.external.cache3.CacheBuilder
import com.nytimes.android.external.cache3.CacheLoader
import com.nytimes.android.external.cache3.LoadingCache
import com.nytimes.android.external.cache3.Supplier
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.random.Random


/**
 * How to poll data with Rx depending on a given value?
 */

typealias Username = String

typealias Token = String

class PollingExercise {

    private enum class Key {
        INSTANCE
    }

    /**
     * Use `private val memoizedTokenSupplier = Suppliers.memoizeWithExpiration({ getLoginToken() }, 2, TimeUnit.SECONDS)`
     * if there's no need to invalidate the Supplier.
     *
     * LoadingCache allows `invalidate(Key.INSTANCE)` which resets the value
     */

    private val tokenCache: LoadingCache<Key, Observable<Token>> = CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build(CacheLoader.from(Supplier { getLoginToken() }))

    fun poll(intervalSeconds: Long) =
            Observable.interval(0, intervalSeconds, TimeUnit.SECONDS)
                    .switchMap { interval ->
                        tokenCache.get(Key.INSTANCE)!!.switchMapSingle { loginToken ->
                            fetchData(interval.toInt()).subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.trampoline())
                                    .doOnError { println("onError $it") }
                                    .retryWhen(RetryWhen.maxRetries(3)
                                            .exponentialBackoff(100, TimeUnit.MILLISECONDS)
                                            .retryWhenInstanceOf(IllegalStateException::class.java)
                                            .build())
                                    .observeOn(Schedulers.computation())
                        }
                    }
                    .startWith(emptyList<Int>())
                    .scan { t1, t2 -> t1.union(t2).toList() }
                    .doOnNext { println("onNext $it") }

    private fun getLoginToken(): Observable<Token> = Observable.just(Random.nextInt(100000).toString()).doOnNext { println("Token: $it") }

    private fun fetchData(seed: Int) = Single.timer(200, TimeUnit.MILLISECONDS).map {
        Random.nextInt(10).takeIf { it > 3 }?.let { generateSequence(seed) { it + 1 }.take(10).toList() }
                ?: throw IllegalStateException()
    }
}

class PollingExerciseTakeUntil() {

    private data class ServerPollingResponse(val jobDone: Boolean)

    fun pollUsingTakeUntil(intervalSeconds: Long, scheduler: Scheduler = Schedulers.computation()) = pollServerPollingResponse()
            .repeatWhen { it.delay(intervalSeconds, TimeUnit.SECONDS, scheduler) }
            .takeUntil { it.jobDone }
            .doOnNext { println("onNext $it") }
            .filter { it.jobDone }
            .subscribe()

    private fun pollServerPollingResponse() = Observable.timer(200, TimeUnit.MILLISECONDS)
            .map { ServerPollingResponse(false) }
}

fun main() {
    val intervalSeconds = 1L
    PollingExercise().pollUsingInterval(intervalSeconds).subscribe()
    PollingExerciseTakeUntil().pollUsingTakeUntil(intervalSeconds)

    Thread.sleep(intervalSeconds * 5 * 1000)
}

