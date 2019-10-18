package playground

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * Display a retry message for every retry and then after 3 retries display an error message
 */

class ShowErrorAfterThreeRetries {

    val single =
            Single.error<Throwable>(IllegalStateException())
                    .retryWhen { errors ->
                        val counter = AtomicInteger(0)
                        errors.takeWhile { counter.getAndIncrement() != 3 }
                                .observeOn(Schedulers.single())
                                .doOnNext { println("Retrying... $counter ${Thread.currentThread().name}") }
                                .observeOn(Schedulers.computation())
                                .flatMap { Flowable.just(counter) }
                    }
}

fun main() {
    val countDownLatch = CountDownLatch(1)
    ShowErrorAfterThreeRetries().single
            .subscribeBy(
                    onSuccess = { "Success!" },
                    onError = {
                        println("Displaying error!!")
                        countDownLatch.countDown()
                    })

    countDownLatch.await()
}
