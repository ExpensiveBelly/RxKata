package utils

import arrow.core.Either
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
fun <T> zip(iterable: List<Single<T>>, defaultWhenEmpty: List<T>? = null) =
    if (defaultWhenEmpty == null || iterable.isNotEmpty()) Single.zip(iterable) { it.map { it as T } }
    else Single.just(defaultWhenEmpty)

@Suppress("UNCHECKED_CAST")
fun <T> combineLatest(iterable: List<Observable<T>>) =
    Observable.combineLatest(iterable) { it.map { it as T } }

fun <T, U> concatScanEager(initialValueSingle: Single<T>, valuesObservable: Observable<U>, accumulator: (T, U) -> T) =
    Observable.concatArrayEager(
        initialValueSingle.map { Either.Left(it) }.toObservable(),
        valuesObservable.map { Either.Right(it) }
    )
        .scan { leftValue, rightValue ->
            Either.Left(
                accumulator(
                    (leftValue as Either.Left).a,
                    (rightValue as Either.Right).b
                )
            )
        }
        .map { (it as Either.Left).a }

fun <T> reactiveProperty(subject: BehaviorSubject<T>, onUpdated: (T) -> Unit = {}): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = subject.value!!

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            subject.onNext(value)
            onUpdated(value)
        }
    }
