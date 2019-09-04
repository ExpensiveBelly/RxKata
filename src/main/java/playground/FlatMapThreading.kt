package playground

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch

/**
 * How does threading propagate outside of the flatmap using subscribeOn?
 */

class FlatMapThreading {

    val flatmap = Observable.range(1, 3)
            .flatMap {
                Observable.just(it)
                        .subscribeOn(Schedulers.io())
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

fun main() {
    val countDownLatch: CountDownLatch = CountDownLatch(1)
    FlatMapThreading().flatmap
            .doOnComplete { countDownLatch.countDown() }
            .subscribe()

    countDownLatch.await()
}