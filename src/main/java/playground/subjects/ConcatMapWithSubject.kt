package playground.subjects

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit


class ConcatMapWithSubject {

    val initialSubject = PublishSubject.create<Int>()
    val secondSubject = PublishSubject.create<Int>()

    val observable = initialSubject
        .concatMap {
            secondSubject
        }

    val flowableIntervalFun: (Scheduler) -> Flowable<Long> = { scheduler: Scheduler ->
        Flowable.interval(0, 50, TimeUnit.MILLISECONDS, scheduler)
            .concatMap {
                Flowable.interval(it.toLong(), 50, TimeUnit.SECONDS, scheduler)
            }
    }
}