package utils

import arrow.core.Option
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

fun <T, U> Observable<T>.mapNotNull(mapper: (T) -> U?) =
    flatMap { mapper(it)?.let { Observable.just(it) } ?: Observable.empty<U>() }

fun <T, U> Maybe<T>.mapNotNull(mapper: (T) -> U?) =
    flatMap { Option(mapper(it)).toMaybe() }

private fun <A> Option<A>.toMaybe(): Maybe<A> = fold(ifEmpty = { Maybe.empty<A>() }, ifSome = { Maybe.just(it) })

fun <T> Single<T>.broadcast(): Single<T> = toObservable().replay(1).refCount().singleOrError()

inline fun <reified U : Any> Observable<*>.filterType() =
    flatMapMaybe {
        if (it is U) Maybe.just(it)
        else Maybe.empty()
    }

fun <T> Observable<Result<T>>.responseOrError() = flatMapSingle {
    if (it.isSuccess) {
        Single.just(it.getOrNull())
    } else {
        Single.just(it.exceptionOrNull())
    }
}

fun <T> Observable<T>.debounceAfterFirst(
    time: Long,
    timeUnit: TimeUnit,
    scheduler: Scheduler = Schedulers.computation()
) =
    firstElement().toObservable().concatWith(skip(1).debounce(time, timeUnit, scheduler))

fun <T> Observable<T>.transformErrors(transform: (Throwable) -> Throwable) = onErrorResumeNext { error: Throwable ->
    Observable.error(transform(error))
}

/**
 * CompositeExceptions can occur when combining multiple parallel observables that have errors at a
 * similar time frame. Operators such as merge, concatEager and switchOnNext are examples of those
 * which can generate these errors.
 * This method will reduce CompositeExceptions when they contain the only one class of exceptions.
 */
fun <T> Observable<T>.sanitiseCompositeException() = transformErrors { error: Throwable ->
    if (error is CompositeException) error.mergeIfSame()
    else error
}

fun CompositeException.mergeIfSame(): Throwable {
    val distinctExceptions = exceptions.distinct()
    return if (distinctExceptions.size == 1) distinctExceptions[0]
    else CompositeException(distinctExceptions)
}

fun <T> Single<T>.cacheAtomicReference(): Single<T> {
    val reference = AtomicReference<T>()
    val referenceShared = doOnSuccess { reference.set(it) }.toObservable().replay(1).refCount().firstOrError()
    return Single.defer {
        val value = reference.get()
        if (value != null) Single.just(value)
        else referenceShared
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> zip(singles: List<Single<T>>): Single<List<T>> =
    if (singles.isNotEmpty()) Single.zip(singles) { list -> list.map { it as T } }
    else Single.just(emptyList())

val mainScheduler = Schedulers.from(Runnable::run)