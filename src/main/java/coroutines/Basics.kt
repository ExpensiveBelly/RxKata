package coroutines

import kotlinx.coroutines.*

/**
 * https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/basics.md
 */

fun main() {
    helloWorld()
    helloWorld2()
    helloWorld3()
    scopeBuilder()
    suspendFunction()
    lightWeight()
}

fun lightWeight() {
    runBlocking {
        repeat(100_000) {
            // launch a lot of coroutines
            launch {
                delay(1000L)
                print(".")
            }
            //this would throw OutOfMemoryError
//            thread {
//                Thread.sleep(1000L)
//                print(".")
//            }
        }
    }
}

fun suspendFunction() {
    runBlocking {
        launch { doWorld() }
        println("Hello,")
    }
}

// this is your first suspending function
suspend fun doWorld() {
    delay(1000L)
    println("World!")
}

fun scopeBuilder() {
    runBlocking {
        // this: CoroutineScope
        launch {
            delay(200L)
            println("Task from runBlocking")
        }

        coroutineScope {
            // Creates a new coroutine scope
            launch {
                delay(500L)
                println("Task from nested launch")
            }

            delay(100L)
            println("Task from coroutine scope") // This line will be printed before nested launch
        }

        println("Coroutine scope is over") // This line is not printed until nested launch completes
    }
}

fun helloWorld3() {
    runBlocking {
        // this: CoroutineScope
        launch {
            // launch new coroutine in the scope of runBlocking
            delay(1000L)
            println("World!")
        }
        println("Hello,")
    }
}

fun helloWorld2() {
    runBlocking {
        //sampleStart
        val job = GlobalScope.launch {
            // launch new coroutine and keep a reference to its Job
            delay(1000L)
            println("World!")
        }
        println("Hello,")
        job.join() // wait until child coroutine completes
//sampleEnd
    }
}

private fun helloWorld() {
    GlobalScope.launch {
        // launch new coroutine in background and continue
        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }
    println("Hello,") // main thread continues while coroutine is delayed
//    Thread.sleep(2000L) // block main thread for 2 seconds to keep JVM alive
    runBlocking {
        // but this expression blocks the main thread
        delay(2000L)  // ... while we delay for 2 seconds to keep JVM alive
    }
}