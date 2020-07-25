package playground

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers
import utils.exponentialBackoffTransformation
import utils.retryWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.time.ExperimentalTime

/**
 * TODO: Retry mechanism; Ability to persist the state and do proper pagination not always starting from the beginning.
 *
 */

class ConcatPaginationExercise {

    private fun fetchPage(number: Int, probabilityOfError: Int = -1) =
        Single.just("Page $number")
            .delay(500, TimeUnit.MILLISECONDS)
            .flatMap { if (Random.nextInt(100) > probabilityOfError) Single.just(it) else throw IllegalStateException() }
            .retryWith(exponentialBackoffTransformation())

    fun fetchNumberOfPages(number: Int) =
        Observable.range(0, number)
            .concatMapSingle { fetchPage(it).subscribeOn(Schedulers.io()) }

    fun fetchNumberOfPagesEagerly(number: Int) =
        Observable.range(0, number)
            .concatMapEager { fetchPage(it).toObservable().subscribeOn(Schedulers.io()) }

    fun fetchNumberOfPagesEagerlyDelayError(number: Int) =
            Observable.range(0, number)
                    .concatMapEagerDelayError({ fetchPage(it, 90).toObservable().subscribeOn(Schedulers.io()) }, true)
}

enum class Choice {
    CONCAT, CONCAT_EAGER, CONCAT_EAGER_DELAY_ERROR
}

@ExperimentalTime
fun main() {
    val countDownLatch = CountDownLatch(1)
    var start = 0L
    val numberOfPages = 5
    val choice = Choice.CONCAT_EAGER_DELAY_ERROR

    ConcatPaginationExercise().run {
        when (choice) {
            Choice.CONCAT -> fetchNumberOfPages(numberOfPages)
            Choice.CONCAT_EAGER -> fetchNumberOfPagesEagerly(numberOfPages)
            Choice.CONCAT_EAGER_DELAY_ERROR -> fetchNumberOfPagesEagerlyDelayError(numberOfPages)
        }
    }.doOnSubscribe { start = System.nanoTime() }
        .doOnComplete {
            println("${TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)}ms")
            countDownLatch.countDown()
        }
        .subscribe(
            { println(it) },
            { println(if (it is CompositeException) it.exceptions else it) })

    countDownLatch.await()
}