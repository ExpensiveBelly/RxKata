package playground.maybe

import io.reactivex.Observable
import io.reactivex.Scheduler
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
}