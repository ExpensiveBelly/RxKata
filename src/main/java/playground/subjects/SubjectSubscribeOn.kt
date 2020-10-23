package playground.subjects

import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.CountDownLatch


/*
https://stackoverflow.com/questions/63331118/publishsubject-subscribeon-behavior/63333117#63333117
 */

fun main() {
    val subject: PublishSubject<Int> = PublishSubject.create()
    val countDownLatch = CountDownLatch(1)

    subject
        .map { it + 1 }
        .subscribeOn(Schedulers.computation())
        .subscribe {
            println(Thread.currentThread().name)
            countDownLatch.countDown()
        }

    Thread.sleep(1000)

    subject.onNext(1)
    countDownLatch.await()
}