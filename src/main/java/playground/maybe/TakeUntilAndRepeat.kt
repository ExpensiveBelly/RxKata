package playground.maybe

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class TakeUntilAndRepeat {

    /*
 `.takeUntil(..)` completes downstream and disposes upstream

 `.onErrorComplete()` completes downstream and because it's been caused by an error, the upstream has been disposed
 and the error propagated downstream till the `.onErrorComplete()

`.  repeat()` positioning determines where the re-subscription will take place. The completion event
will not propagate downwards once caught by `.repeat()`
 */

    val observableTakeUntilFun = { scheduler: Scheduler ->
        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler)
            .take(2)
//            .repeat() // Neither "Interval complete before repeat()" nor "Interval complete after repeat()" are printed
            .doOnNext { println("Emission: $it") }
            .doOnDispose { println("Disposed") }
            .repeat() // Neither "Interval complete before repeat()" nor "Interval complete after repeat()" are printed. the flatMapCompletable downstream is NOT completed
            .doOnComplete { println("Interval complete before repeat()") }
            .flatMapCompletable {
//                Completable.error(NullPointerException())
                Completable.fromCallable { "Hello" }
                    .doOnSubscribe { println("Completable subscribed") }
                    .doOnDispose { println("Completable disposed") }
                    .delay(500, TimeUnit.MILLISECONDS, scheduler)
                    .doOnComplete { println("Completable before onErrorComplete $it") }
                    .onErrorComplete()
                    .doOnComplete { println("Completable after onErrorComplete $it") }
            }
            .repeat() // "Interval complete before repeat()", "Completable before onErrorComplete", "Completable after onErrorComplete is printed"
            .doOnComplete { println("Interval complete after repeat()") }
//            .repeat() // "Interval complete before repeat()" and "Interval complete after repeat()", "Completable before onErrorComplete", "Completable after onErrorComplete is printed" are printed
    }
}

fun main() {
    TakeUntilAndRepeat().apply {
        observableTakeUntilFun(Schedulers.trampoline()).subscribe { println("Subscribe onComplete") }
        Thread.sleep(600)
    }

}