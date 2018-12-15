/**
 * The MIT License (MIT)

Copyright (c) 2015 Daniel Lew

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package danlew

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

object Sample {

    @JvmStatic
    fun main(args: Array<String>) {
        val sources = Sources()

        // Create our sequence for querying best available data
        val source = Observable.concat(
                sources.memory(),
                sources.disk(),
                sources.network())
                .filter { it.isPresent && it.get().isUpToDate }
                .firstElement()
                .toObservable()

        // "Request" latest data once a second
        Observable.interval(1, TimeUnit.SECONDS)
                .flatMap { source }
                .subscribe { System.out.println("Received: " + it.get().value) }

        // Occasionally clear memory (as if app restarted) so that we must go to disk
        Observable.interval(3, TimeUnit.SECONDS)
                .subscribe { sources.clearMemory() }

        // Java will quit unless we idle
        sleep((15 * 1000).toLong())
    }

    private fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            // Ignore
        }

    }
}