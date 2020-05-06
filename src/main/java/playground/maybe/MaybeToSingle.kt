package playground.maybe

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MaybeToSingle {

    val single = Observable.just(true)
        .filter(true::equals)
        .repeat()
        .singleOrError()

    val subject = PublishSubject.create<Unit>()
    val subjectStop = PublishSubject.create<Unit>()


    val observableFun = { scheduler: Scheduler ->
        subject
            .flatMap {
                Observable.interval(500, TimeUnit.MILLISECONDS, scheduler)
                    .takeUntil(subjectStop)
                    .repeat()
            }
    }

    val observableTakeUntilFun = { scheduler: Scheduler ->
        Observable.interval(500, TimeUnit.MILLISECONDS, scheduler)
            .take(2)
//            .repeat() // Neither "Interval complete before repeat()" nor "Interval complete after repeat()" are printed
            .doOnNext { println("Emission: $it") }
            .doOnDispose { println("Disposed") }
            .takeUntil(subjectStop.doOnNext { println("subjectStop") })
//            .repeat() // Neither "Interval complete before repeat()" nor "Interval complete after repeat()" are printed
            .doOnComplete { println("Interval complete before repeat()") }
            .flatMapCompletable {
//                Completable.error(NullPointerException())
                Completable.fromCallable { "Hello" }
                    .doOnSubscribe { println("Completable subscribed") }
//                            .delay(5, TimeUnit.SECONDS, scheduler)
                    .doOnComplete { println("Completable before onErrorComplete $it") }
                    .onErrorComplete()
                    .doOnComplete { println("Completable after onErrorComplete $it") }
            }
            .repeat() // "Interval complete before repeat()" is printed
            .doOnComplete { println("Interval complete after repeat()") }
//            .repeat() // "Interval complete before repeat()" and "Interval complete after repeat()" are printed
    }
}

fun main() {
    /*
     `.takeUntil(..)` completes downstream and disposes upstream

     `.onErrorComplete()` completes downstream and because it's been caused by an error, the upstream has been disposed
     and the error propagated downstream till the `.onErrorComplete()

`.  repeat()` positioning determines where the re-subscription will take place. The completion event
will not propagate downwards once caught by `.repeat()`
     */
    MaybeToSingle().apply {
        observableTakeUntilFun(Schedulers.trampoline()).subscribe { println("Subscribe onComplete") }
        Thread.sleep(600)
//        subjectStop.onNext(Unit)
    }

    // `repeat` positioning in the stream determines where the completion even finishes propagating.

}