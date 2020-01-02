package playground.threading

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CombineLatestThreading {

    /**
     * Which thread will be printed?
     *
     * Always the last emission. If the interval is the last one to emit, then it will be computation (RxComputationThreadPool).
     * If you tweak the sleep numbers for the Thread.sleep and make `loadData` emit first by increasing the delay to 1 second for instance then the
     * io thread is printed (RxCachedThreadScheduler)
     */

    val combineLatestThreading = Observables.combineLatest(
        Observable.interval(
            800,
            TimeUnit.MILLISECONDS
        ).doOnNext { println("interval emission ${Thread.currentThread().name}") },
        Observable.range(1, 3), loadData(), loadMoreData()
    ) { t1, t2, t3, t4 ->
        println("CombineLatest emission: ${Thread.currentThread().name}")
    }

    private fun loadData() = Observable.fromCallable {
        Thread.sleep(500)
        "Data"
    }.doOnNext { println("loadData emission ${Thread.currentThread().name}") }
        .subscribeOn(Schedulers.io())

    private fun loadMoreData() = Observable.fromCallable {
        Thread.sleep(300)
        "MoreData"
    }.doOnNext { println("loadMoreData emission ${Thread.currentThread().name}") }
        .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(2)))
}

fun main() {

    CombineLatestThreading().combineLatestThreading.blockingSubscribe()
}