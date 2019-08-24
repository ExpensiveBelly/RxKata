package playground

import com.github.davidmoten.rx2.RetryWhen
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * How to poll data with Rx depending on a given value?
 *
 * TODO:
 * - Fetch login key and use that to fetch data
 * - Aggregate lists using `scan`
 * - Tests
 */

class PollingExercise {

    fun poll(intervalSeconds: Long) =
            Observable.interval(0, intervalSeconds, TimeUnit.SECONDS)
                    .switchMapSingle {
                        fetchData(it.toInt()).subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.trampoline())
                                .doOnError { println("onError $it") }
                                .retryWhen(RetryWhen.maxRetries(3)
                                        .exponentialBackoff(100, TimeUnit.MILLISECONDS)
                                        .retryWhenInstanceOf(IllegalStateException::class.java)
                                        .build())
                    }
                    .startWith(emptyList<Int>())
                    .doOnNext { println("onNext $it") }


    private fun fetchData(seed: Int) = Single.timer(200, TimeUnit.MILLISECONDS).map {
        Random.nextInt(10).takeIf { it > 4 }?.let { generateSequence(seed) { it + 1 }.take(10).toList() }
                ?: throw IllegalStateException()
    }
}

fun main() {
    val intervalSeconds = 1L
    PollingExercise().poll(intervalSeconds).subscribe()

    Thread.sleep(intervalSeconds * 5 * 1000)
}