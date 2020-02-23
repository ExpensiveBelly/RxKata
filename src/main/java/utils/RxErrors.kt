package utils

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlin.math.min


fun <T> Single<T>.retryWith(transformation: (Throwable) -> Single<*>): Single<T> =
    retryWhen { errors -> errors.flatMapSingle(transformation) }

fun exponentialBackoffTransformation(
    initialDelayMs: Long = 100L,
    capDelayMs: Long = 30000L,
    multiplier: Long = 2L,
    scheduler: Scheduler = Schedulers.computation()
) =
    variableDelayTransformation<Throwable>(initialDelayMs, scheduler) { delayMs ->
        min(
            capDelayMs,
            delayMs * multiplier
        )
    }