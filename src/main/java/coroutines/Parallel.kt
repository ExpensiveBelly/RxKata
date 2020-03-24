package coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun <A, B> List<A>.pmap(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
    f: suspend (A) -> B
): List<B> =
    runBlocking {
        map { async(coroutineDispatcher) { f(it) } }.map { it.await() }
    }

fun <A> List<A>.forEachParallel(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
    f: suspend (A) -> Unit
) = runBlocking {
    map { async(coroutineDispatcher) { f(it) } }.map { it.await() }
}