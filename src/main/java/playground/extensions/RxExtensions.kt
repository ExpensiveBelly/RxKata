package playground.extensions

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicReference

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

fun <T, U> concatScanEager(initialValueSingle: Single<T>, valuesObservable: Observable<U>, accumulator: (T, U) -> T) =
        Observable.concatArrayEager(
                initialValueSingle.map { Either.Left(it) }.toObservable(),
                valuesObservable.map { Either.Right(it) }
        )
                .scan { leftValue, rightValue -> Either.Left(accumulator((leftValue as Either.Left).value, (rightValue as Either.Right).value)) }
                .map { (it as Either.Left).value }

sealed class Either<out A, out B> {
    class Left<A>(val value: A) : Either<A, Nothing>()
    class Right<B>(val value: B) : Either<Nothing, B>()
}

fun <T> Single<T>.broadcast() = toObservable().replay().refCount(1)