@file:Suppress("NOTHING_TO_INLINE")

import io.reactivex.*

/**
 * https://gist.github.com/tomaszpolanski/99c37c388e06e57ef72a5c8e752b8c2c
 */

inline fun <reified T> printEvent(tag: String, success: T?, error: Throwable?): Any =
        when {
            success == null && error == null -> Log.d(tag, "Complete") /* Only with Maybe */
            success != null -> Log.d(tag, "Success $success")
            error != null -> Log.d(tag, "Error $error")
            else -> -1 /* Cannot happen*/
        }

inline fun printEvent(tag: String, error: Throwable?) =
        when {
            error != null -> Log.d(tag, "Error $error")
            else -> Log.d(tag, "Complete")
        }

/**
 * Example usage of [log]:
 *   Single.timer(1, TimeUnit.SECONDS).log().subscribe({ }, { })
 */

inline fun tag() =
        Thread.currentThread().stackTrace
                .first { it.fileName.endsWith(".kt") }
                .let { stack -> "${stack.fileName.removeSuffix(".kt")}::${stack.methodName}:${stack.lineNumber}" }

inline fun <reified T> Single<T>.log(tag: String = tag()): Single<T> {
    return doOnEvent { success, error -> printEvent(tag, success, error) }
            .doOnSubscribe { Log.d(tag, "Subscribe") }
            .doOnDispose { Log.d(tag, "Dispose") }
}

inline fun <reified T> Maybe<T>.log(tag: String = tag()): Maybe<T> {
    return doOnEvent { success, error -> printEvent(tag, success, error) }
            .doOnSubscribe { Log.d(tag, "Subscribe") }
            .doOnDispose { Log.d(tag, "Dispose") }
}

inline fun Completable.log(tag: String = tag()): Completable {
    return doOnEvent { printEvent(tag, it) }
            .doOnSubscribe { Log.d(tag, "Subscribe") }
            .doOnDispose { Log.d(tag, "Dispose") }
}

inline fun <reified T> Observable<T>.log(tag: String = tag()): Observable<T> {
    return doOnEach { Log.d(tag, "Each $it") }
            .doOnSubscribe { Log.d(tag, "Subscribe") }
            .doOnDispose { Log.d(tag, "Dispose") }
}

inline fun <reified T> Flowable<T>.log(tag: String = tag()): Flowable<T> {
    return doOnEach { Log.d(tag, "Each $it") }
            .doOnSubscribe { Log.d(tag, "Subscribe") }
            .doOnCancel { Log.d(tag, "Cancel") }
}


object Log {
    fun d(tag: String, message: String) {
        println("$tag : $message")
    }

}