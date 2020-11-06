package coroutines

import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.UncaughtExceptionCaptor
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
val xHandlerTopScope = TestCoroutineExceptionHandler()

val xHandlerOverride = TestCoroutineExceptionHandler()

val xHandlerChildScope = TestCoroutineExceptionHandler()

private object ThrownException : Throwable("ThrownException")

/**
 * https://github.com/Kotlin/kotlinx.coroutines/issues/1157
 *
 * https://www.lukaslechner.com/why-exception-handling-with-kotlin-coroutines-is-so-hard-and-how-to-successfully-master-it/
 */

@ExperimentalCoroutinesApi
class ExceptionHandlingForLaunchTest {
    private val dispatcher = TestCoroutineDispatcher()

    private var uncaughtException: Throwable? = null

    @Before
    fun setup() {
        Thread.setDefaultUncaughtExceptionHandler { _, e -> uncaughtException = e }
    }

    @After
    fun tearDown() {
        xHandlerTopScope.cleanupTestCoroutines()
        xHandlerOverride.cleanupTestCoroutines()
        xHandlerChildScope.cleanupTestCoroutines()

        Thread.setDefaultUncaughtExceptionHandler(null)
        uncaughtException = null
    }

    @Test
    fun `When no CoroutineExceptionHandler is installed at all, exception is uncaught`() {
        CoroutineScope(dispatcher).run {
            launch {
                delay(1000)
                throw ThrownException
            }

            dispatcher.advanceTimeBy(1000)
            assertTrue(uncaughtException is ThrownException)
        }
    }

    @Test
    fun `exception is caught because it is a top coroutine`() {
        CoroutineScope(dispatcher).run {
            val verifier = mockk<Verifier>(relaxed = true)
            launch {
                try {
                    throw RuntimeException("RuntimeException in coroutine")
                } catch (exception: Exception) {
                    verifier.catch()
                }
            }
            verify { verifier.catch() }
        }
    }

    @Test
    fun `When CoroutineExceptionHandler is installed in top-scope, exception is handled by top-scope handler`() {
        CoroutineScope(xHandlerTopScope + dispatcher).run {
            launch {
                delay(1000)
                throw ThrownException
            }

            dispatcher.advanceTimeBy(1000)

            assertThat(uncaughtException, nullValue())
            assertEquals(listOf<Throwable>(ThrownException), xHandlerTopScope.uncaughtExceptions)
        }
    }

    @Test
    fun `When CoroutineExceptionHandler is *added* to top-scope, exception is handled by the added handler`() {
        CoroutineScope(dispatcher).run {
            launch(xHandlerOverride) {
                delay(1000)
                throw ThrownException
            }

            dispatcher.advanceTimeBy(1000)

            assertThat(uncaughtException, nullValue())
            assertEquals(listOf<Throwable>(ThrownException), xHandlerOverride.uncaughtExceptions)
        }
    }

    @Test
    fun `When CoroutineExceptionHandler is *overridden* in top-scope, exception is handled by overriding handler`() {
        CoroutineScope(xHandlerTopScope + dispatcher).run {
            launch(xHandlerOverride) {
                delay(1000)
                throw ThrownException
            }

            dispatcher.advanceTimeBy(1000)

            assertThat(uncaughtException, nullValue())
            assertEquals(emptyList<Throwable>(), xHandlerTopScope.uncaughtExceptions)
            assertEquals(listOf<Throwable>(ThrownException), xHandlerOverride.uncaughtExceptions)
        }
    }

    /*
    withContext never invokes its uncaught exception handler.
     */

    @Test
    fun `When CoroutineExceptionHandler is added to child-scope, exception is still handled by top-scope`() {
        CoroutineScope(xHandlerTopScope + dispatcher).run {
            launch {
                withContext(xHandlerChildScope) {
                    delay(1000)
                    throw ThrownException
                }
            }

            dispatcher.advanceTimeBy(1000)

            assertThat(uncaughtException, nullValue())
            assertEquals(listOf<Throwable>(ThrownException), xHandlerTopScope.uncaughtExceptions)
            assertEquals(emptyList<Throwable>(), xHandlerChildScope.uncaughtExceptions)
        }
    }

    @Test
    fun `withContext needs to catch its own exception or be caught outside`() {
        val verifier = mockk<Verifier>(relaxed = true)
        suspend fun loadData() = withContext(Dispatchers.IO) {
            try {
                println("about to throw ThrownException")
                throw ThrownException
            } catch (e: Throwable) {
                verifier.catch()
            }
        }

        suspend fun loadMoreData(): Unit = withContext(Dispatchers.IO) {
            throw ThrownException
        }

        runBlocking {
            loadData()

            try {
                loadMoreData()
            } catch (e: Throwable) {
                verifier.catch()
            }

            verify(exactly = 2) { verifier.catch() }
        }
    }

    @Test
    fun `When CoroutineExceptionHandler is added to child-scope, exception is still handled by overriding top-scope`() {
        CoroutineScope(dispatcher).run {
            launch(xHandlerOverride) {
                launch(xHandlerChildScope) { //launch(xHandlerChildScope) really only matters for coroutines without parent.
                    delay(1000)
                    //CoroutineExceptionHandler on child coroutines doesn’t have any effect.
                    throw ThrownException
                }
            }

            dispatcher.advanceTimeBy(1000)

            assertThat(uncaughtException, nullValue())
            assertEquals(listOf<Throwable>(ThrownException), xHandlerOverride.uncaughtExceptions)
            assertEquals(emptyList<Throwable>(), xHandlerChildScope.uncaughtExceptions)
        }
    }

    @Test
    fun `coroutineExceptionHandler on child coroutines does not have any effect`() {
        CoroutineScope(dispatcher).run {
            launch(xHandlerOverride) {
                launch {
                    delay(1000)
                    throw ThrownException
                }
            }

            dispatcher.advanceTimeBy(1000)

            assertThat(uncaughtException, nullValue())
            assertEquals(listOf<Throwable>(ThrownException), xHandlerOverride.uncaughtExceptions)
            assertEquals(emptyList<Throwable>(), xHandlerChildScope.uncaughtExceptions)
        }
    }

    @Test
    fun `When CoroutineExceptionHandler is added to child-scope in a Job, exception is handled by overriding handler`() {
        CoroutineScope(dispatcher).run {
            launch(xHandlerOverride) {
                launch(Job() + xHandlerChildScope) {
                    delay(1000)
                    throw ThrownException
                }
            }

            dispatcher.advanceTimeBy(1000)

            assertThat(uncaughtException, nullValue())
            assertEquals(listOf<Throwable>(ThrownException), xHandlerChildScope.uncaughtExceptions)
            assertEquals(emptyList<Throwable>(), xHandlerOverride.uncaughtExceptions)
        }
    }

    @Test
    fun `When no top-scope CoroutineExceptionHandler is installed, added or overridden, exception always remains uncaught`() {
        CoroutineScope(dispatcher).run {
            launch {
                launch(xHandlerChildScope) {
                    delay(1000)
                    throw ThrownException
                }
            }

            dispatcher.advanceTimeBy(1000)

            assertTrue(uncaughtException is Throwable)
            assertEquals(emptyList<Throwable>(), xHandlerChildScope.uncaughtExceptions)
        }
    }

    @Test
    fun `When top-scope CoroutineExceptionHandler is installed and not overriden by a parent coroutine then exception is handled by top scope`() {
        CoroutineScope(xHandlerTopScope + dispatcher).run {
            launch {
                launch(xHandlerChildScope) {
                    delay(1000)
                    throw ThrownException
                }
            }

            dispatcher.advanceTimeBy(1000)

            assertThat(uncaughtException, nullValue())
            assertEquals(listOf<Throwable>(ThrownException), xHandlerTopScope.uncaughtExceptions)
            assertEquals(emptyList<Throwable>(), xHandlerChildScope.uncaughtExceptions)
        }
    }

    /*
    https://medium.com/androiddevelopers/cancellation-in-coroutines-aa6b90163629
     */

    @Test(expected = Exception::class) //Exception bubbles up b
    fun `When async throws an Exception by calling await() it cancels the scope and Exception is thrown`() {
        runBlocking {
            try {
                val x = async {
                    throw Exception()
                    10
                }
                println("Awaiting...")
                x.await()
            } catch (e: Exception) {
                println("Catch Exception")
                println("Is this coroutine active? " + this.isActive)
                delay(1) //This is a suspend function, on an already cancelled coroutine
                println("This is never printed")
            }
        }
    }

    @Test(expected = Exception::class) //Exception bubbles up b
    fun `When async throws an Exception by calling await() it cancels the scope and a suspend function can be called if NonCancellable`() {
        runBlocking {
            try {
                val x = async {
                    throw Exception()
                    10
                }
                println("Awaiting...")
                x.await()
            } catch (e: Exception) {
                println("Catch Exception")
                println("Is this coroutine active? " + this.isActive)
                withContext(NonCancellable) {} //This is a suspend function, with NonCancellable, so it will go through
                println("This is printed")
            }
        }
    }

    private interface Verifier {
        fun awaiting()
        fun catch()
        fun catchAfterDelay()
    }

    @Test
    fun `When async throws an Exception by calling await() it cancels the scope unless it is wrapped in a supervisorScope`() {
        runBlocking {
            supervisorScope {
                val verifier = mockk<Verifier>(relaxed = true)
                try {
                    val x = async {
                        throw Exception()
                        10
                    }
                    verifier.awaiting()
                    x.await()
                } catch (e: Exception) {
                    verifier.catch()
                    delay(1)
                    verifier.catchAfterDelay()
                }

                verifyOrder {
                    verifier.awaiting()
                    verifier.catch()
                    verifier.catchAfterDelay()
                }
            }
        }
    }

    @Test
    fun `When async throws an exception inside a launch with a handler and a Job it gets caught by the exception handler`() {
        suspend fun oops() {
            delay(1000)
            throw ThrownException
        }

        CoroutineScope(dispatcher).run {
            launch(xHandlerTopScope) {
                async { oops() }
            }
        }

        dispatcher.advanceTimeBy(1000)

        assertEquals(listOf<Throwable>(ThrownException), xHandlerTopScope.uncaughtExceptions)
    }

    @Test
    fun `the scoping function coroutineScope re-throws uncaught exceptions of its child Coroutines and so we can handle them with try-catch`() {
        val verifier = mockk<Verifier>(relaxed = true)
        CoroutineScope(dispatcher).run {
            launch {
                try {
                    coroutineScope {
                        launch {
                            throw ThrownException
                        }
                    }
                } catch (th: Throwable) {
                    verifier.catch()
                }
            }
        }
        verify { verifier.catch() }
    }
}

@ExperimentalCoroutinesApi
class TestCoroutineExceptionHandler :
    AbstractCoroutineContextElement(CoroutineExceptionHandler), UncaughtExceptionCaptor, CoroutineExceptionHandler {
    private val _exceptions = mutableListOf<Throwable>()

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        synchronized(_exceptions) {
            _exceptions += exception
        }
    }

    override val uncaughtExceptions
        get() = synchronized(_exceptions) { _exceptions.toList() }

    override fun cleanupTestCoroutines() {
        synchronized(_exceptions) {
            _exceptions.clear()
        }
    }
}