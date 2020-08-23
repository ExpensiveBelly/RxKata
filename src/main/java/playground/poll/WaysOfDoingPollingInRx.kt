package playground.poll

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import utils.mainScheduler
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface PollingMockForTesting {
    fun next()
    fun error()
}

@ExperimentalTime
class WaysOfDoingPollingInRx(private val mockForTesting: PollingMockForTesting) {

    fun intervalPolling(time: Duration, stopSource: Single<Unit>) {
        Observable.interval(time.toLongMilliseconds(), TimeUnit.MILLISECONDS)
            .concatMapSingle(transformation())
            .subscribeOn(Schedulers.io())
            .observeOn(mainScheduler)
            .doOnNext { mockForTesting.next() }
            .takeUntil(stopSource.toObservable())
    }

    fun timerWithRepeat(time: Duration) {
        Single.timer(time.toLongMilliseconds(), TimeUnit.MILLISECONDS)
            .flatMap(transformation())
            .subscribeOn(Schedulers.io())
            .observeOn(mainScheduler)
            .doOnSuccess { mockForTesting.next() }
            .repeat()
    }

    fun createObservableScheduler(time: Duration) {
        Observable.create<Long> {
            Schedulers.newThread().schedulePeriodicallyDirect(
                { mockForTesting.next() },
                time.toLongMilliseconds(),
                time.toLongMilliseconds(),
                TimeUnit.MILLISECONDS
            )
        }
    }

    fun waitTillItCompletesOrErrors(time: Duration) {
        Observable.fromCallable { "Data" }
            .flatMapSingle(transformation())
            .repeatWhen { observable ->
                observable.concatMap {
                    Observable.timer(
                        time.toLongMilliseconds(),
                        TimeUnit.MILLISECONDS
                    )
                }.takeUntil { it < 3L }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(mainScheduler)
            .doOnNext { mockForTesting.next() }
            .doOnError { mockForTesting.error() } //It would need to refine the `retryWhen` strategy
            .retry()
    }

    private fun transformation(): (Any) -> Single<String> = {
        if (Random.nextBoolean()) {
            Single.just("Data $it in thread: ${Thread.currentThread().name}")
                .subscribeOn(Schedulers.io())
                .retryWhen { it }
        } else Single.error(IllegalStateException())
    }
}