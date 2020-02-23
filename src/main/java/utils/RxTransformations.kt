package utils

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong


fun <T> variableDelayTransformation(
    initialDelayMs: Long,
    scheduler: Scheduler = Schedulers.computation(),
    delayUpdater: (Long) -> Long
) =
    AtomicLong(initialDelayMs).let { counter ->
        { obj: T ->
            val count = counter.get()
            val newCount = delayUpdater(count)
            counter.set(newCount)
            Single.timer(newCount, TimeUnit.MILLISECONDS, scheduler).map { obj }
        }
    }