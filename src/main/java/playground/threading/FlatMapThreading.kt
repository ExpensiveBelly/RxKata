package playground.threading

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch

/**
 * How does threading propagate outside of the flatmap using subscribeOn?
 */

class FlatMapThreading {

    val flatmap = Observable.range(1, 300)
        .doOnNext { println(currentThreadName) }
        .flatMap {
            Observable.just(it)
                .doOnSubscribe { println("First doOnSubscribe: $currentThreadName") }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { println("Second doOnSubscribe: $currentThreadName") }
                .subscribeOn(Schedulers.computation())
                .doOnNext { println("First flatmap : $it : ${Thread.currentThread().name}") }
        }
        .flatMap {
            Observable.range(it, 2)
                .doOnNext { println("Second flatmap : $it : ${Thread.currentThread().name}") }
        }
        .flatMap {
            Observable.range(it, 1)
                .subscribeOn(Schedulers.newThread())
                .doOnNext { println("Third flatmap :  $it: ${Thread.currentThread().name}") }
        }.observeOn(Schedulers.computation())
        .doOnNext { println("After observeOn: ${Thread.currentThread().name}") }
}

private val currentThreadName
    get() = Thread.currentThread().name

fun main() {
    val countDownLatch = CountDownLatch(1)
    FlatMapThreading().flatmap
        .doOnComplete { countDownLatch.countDown() }
        .subscribe()

    countDownLatch.await()
}