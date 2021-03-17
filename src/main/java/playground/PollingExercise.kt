package playground

import cacheValues
import com.dropbox.android.external.cache3.Cache
import com.dropbox.android.external.cache3.CacheBuilder
import combine
import countErrorTransformation
import exponentialBackoffTransformation
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import predicateErrorTransformation
import retryWith
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

    private val tokenCache: Cache<Key, Single<Token>> = CacheBuilder.newBuilder()
        .maximumSize(1)
        .expireAfterWrite(2, TimeUnit.SECONDS)
        .build()

    fun pollUsingInterval(intervalSeconds: Long) =
        Observable.interval(0, intervalSeconds, TimeUnit.SECONDS)
            .switchMapSingle { interval ->
                tokenCache.get(Key.INSTANCE) { getLoginToken() }.flatMap { loginToken ->
                    fetchData(interval.toInt()).subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.trampoline())
                        .retryWith(
                            combine(
                                countErrorTransformation(3),
                                exponentialBackoffTransformation(),
                                predicateErrorTransformation { it is IllegalStateException }
                            )
                        )
                        .observeOn(Schedulers.computation())
                }
            }
            .startWithItem(emptyList<Int>())
            .scan { t1, t2 -> t1.union(t2).toList() }
            .doOnNext { println("onNext $it") }

    private fun getLoginToken(): Single<Token> =
        Observable.defer { Observable.just(Random.nextInt(100000).toString()).doOnNext { println("Token: $it") } }
            .firstOrError().cacheValues()

    private fun fetchData(seed: Int) = Single.timer(200, TimeUnit.MILLISECONDS).map {
        Random.nextInt(10).takeIf { it > 3 }?.let { generateSequence(seed) { it + 1 }.take(10).toList() }
            ?: throw IllegalStateException()
    }
}

class PollingExerciseTakeUntil() {

    private data class ServerPollingResponse(val jobDone: Boolean)

    fun pollUsingTakeUntil(intervalSeconds: Long, scheduler: Scheduler = Schedulers.computation()) =
        pollServerPollingResponse()
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