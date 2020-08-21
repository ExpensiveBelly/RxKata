package coroutines

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.await
import kotlinx.coroutines.selects.select
import java.util.concurrent.TimeUnit

/*
Coroutines wait for the children to finish
 */

fun main() {
    runBlocking {
        val out = coroutineScope {
            println("scope ${Thread.currentThread()}")
            val await1 = async { Single.timer(1, TimeUnit.SECONDS, Schedulers.computation()).await() }
            val await2 = async { Single.never<Unit>().await() }

            val returned = select<Int> {
                await1.onAwait { 1 }
                await2.onAwait { 2 }
            }
            println("select $returned ${Thread.currentThread()}")

            await2.cancel() //If this is omitted then the coroutineScope will never finish

            returned
        }

        println("out : $out ${Thread.currentThread()}")
    }
}