package operators

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables

/**
 * DistinctUntilChanged implementation using other Rx operators
 */

fun <T : Any> Observable<T>.distinctUntilChanged2() =
    Observables.zip(this, skip(1))
        .filter { (a: T, b: T) ->
            println("$a to $b")
            a != b
        }
        .map { (_: T, b: T) -> b }
        .startWith(firstOrError())
