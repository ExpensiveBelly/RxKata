package playground.extensions

import io.reactivex.Single
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

fun <T> zip(singles: List<Single<T>>): Single<List<T>> =
        if (singles.isNotEmpty()) Single.zip(singles) { list -> list.map { it as T } }
        else Single.just(emptyList())