package coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test


class AsyncDeferredTest {

    @Test
    fun `calling await multiple times should execute call once`() {
        runBlocking {
            val async = async { loadData() }
            async.await()
            async.await()
            async.await()
        }
    }

    private suspend fun loadData() = delay(1000).also { println("Loading...") }
}