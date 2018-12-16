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
import io.reactivex.ObservableTransformer
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Simulates three different sources - one from memory, one from disk,
 * and one from network. In reality, they're all in-memory, but let's
 * play pretend.
 *
 * Observable.create() is used so that we always return the latest data
 * to the subscriber; if you use just() it will only return the data from
 * a certain point in time.
 */
class Sources {

    // Create our sequence for querying best available data
    val source: Observable<Optional<Data>> = Observable.concat(
            memory(),
            disk(),
            network())
            .filter { it.isPresent && it.get().isUpToDate }
            .firstElement()
            .toObservable()

    // Memory cache of data
    private var memory: Optional<Data> = Optional.empty()

    // What's currently "written" on disk
    private var disk: Optional<Data> = Optional.empty()

    // Each "network" response is different
    private var requestNumber = AtomicInteger(0)

    // In order to simulate memory being cleared, but data still on disk
    fun clearMemory() {
        println("Wiping memory...")
        memory = Optional.empty()
    }

    fun clearDisk() {
        println("Wiping disk...")
        disk = Optional.empty()
    }

    fun memory(): Observable<Optional<Data>> = Observable.create<Optional<Data>> { emitter ->
        emitter.onNext(memory)
        emitter.onComplete()
    }.compose(logSource("MEMORY"))

    fun disk(): Observable<Optional<Data>> = Observable.create<Optional<Data>> { emitter ->
        emitter.onNext(disk)
        emitter.onComplete()
    }
            .doOnNext { memory = it }
            .compose(logSource("DISK"))


    fun network(): Observable<Optional<Data>> = Observable.create<Optional<Data>> {
        it.onNext(Optional.of(Data("Server Response #${requestNumber.incrementAndGet()}")))
        it.onComplete()
    }
//            .delay(500, TimeUnit.MILLISECONDS)
            .share()
            .doOnNext {
                memory = it
                disk = it
            }
            .compose(logSource("NETWORK"))


    // Simple logging to let us know what each source is returning
    private fun logSource(source: String): ObservableTransformer<Optional<Data>, Optional<Data>> {
        return ObservableTransformer { dataObservable ->
            dataObservable.doOnNext { data ->
                if (!data.isPresent) {
                    println("$source does not have any data.")
                } else if (!data.get().isUpToDate) {
                    println("$source has stale data.")
                } else {
                    println("$source has the data you are looking for!")
                }
            }
        }
    }


}