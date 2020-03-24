package coroutines

import kotlinx.coroutines.*
import org.junit.Assert.assertTrue
import org.junit.Test


class CancellationHandlerKtTest {

    @Test
    fun `exception is handled by inner`() {
        var handled = false

        val innerExceptionHandler1 = CoroutineExceptionHandler { _, throwable ->
            handled = true
        }
        runBlocking {
            supervisorScope {
                val job7 = launch(innerExceptionHandler1) {
                    throw Exception("job7")
                }
                val job8 = launch {
                    delay(100)
                }
                joinAll(job7, job8)
            }
        }

        assertTrue(handled)
    }

    @Test
    fun `exception is handled by outer`() {
        var handled = false

        val outerExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            handled = true
        }
        val innerExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            handled = false
        }

        runBlocking {
            supervisorScope {
                val job78 = launch(outerExceptionHandler) {
                    val job7 = launch(innerExceptionHandler) {
                        throw Exception("job7")
                    }
                    val job8 = launch {
                        delay(100)
                        println("this job8 will not run")
                    }
                    joinAll(job7, job8)
                }
                job78.join()
            }
        }

        assertTrue(handled)
    }
}