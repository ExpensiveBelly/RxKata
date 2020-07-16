package playground.subjects

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

/**
 * Subject Exercises
 */

class Exercise1 {

    init {
        println("Exercise 1")
    }

    val subject = PublishSubject.create<Int>()

    val combined: Observable<Int> = subject.flatMap { Observable.empty<Int>() }.concatWith(Observable.error(IllegalStateException()))

    fun run() {
        combined.doOnNext { println("onNext: $it") }
                .doOnError { println("onError: $it") }
                .doOnComplete { println("onComplete") }
                .test()
                .assertNoValues()

        // TODO: What's the output? What will it print?
        subject.onNext(1)
    }
}

class Exercise2 {
    init {
        println("Exercise 2")
    }

    data class MyData(val key: Int, val value: String)

    val myList: List<MyData> = listOf(
        MyData(
            1,
            "Hello"
        ), MyData(2, "CMC")
    )

    //What transformations are needed to transform `myList` into `myMap`?
//    val myMap: Map<Int, String> =
}

class Exercise3 {

    init {
        println("Exercise 3")
    }

    val subject = PublishSubject.create<Int>()

    val combined = subject.singleOrError().flatMap { Single.just(1) }.concatWith(Single.error(IllegalStateException()))

    fun run() {
        combined.doOnNext { println("onNext: $it") }
                .doOnError { println("onError: $it") }
                .doOnComplete { println("onComplete") }
                .test().assertNoValues()

        // TODO: What's the output? What will it print?
        subject.onNext(1)
    }
}

class Exercise4 {

    init {
        println("Exercise 4")
    }

    val subject = PublishSubject.create<Int>()

    val combined = subject.firstOrError().flatMap { Single.just(1) }.concatWith(Single.error(IllegalStateException()))

    fun run() {
        combined.doOnNext { println("onNext: $it") }
            .doOnError { println("onError: $it") }
            .doOnComplete { println("onComplete") }
            .test().assertNoValues()

        // TODO: What's the output? What will it print?
        subject.onNext(1)
    }
}

class Exercise5 {

    init {
        println("Exercise 5")

        val observable: Observable<Observable<Int>> = Observable.just(1).map { Observable.just(it) }

        //Transform `Observable<Observable<Int>>` to `Observable<Int>`
        fun run() {
            val flattened: Observable<Int> = observable.flatMap { it }
            val flattened4: Observable<Int> = observable.mergeAll()
            val flattened2: Observable<Int> = observable.concatAll()
            val flattened3: Observable<Int> = observable.switchLatest()
        }
    }
}

/** * Merges the emissions of an Observable<Observable<T>>. Same as calling `flatMap { it }`.
 */

private fun <T : Any> Observable<Observable<T>>.mergeAll(): Observable<T> = flatMap { it }
private fun <T : Any> Observable<Observable<T>>.concatAll(): Observable<T> = concatMap { it }
private fun <T : Any> Observable<Observable<T>>.switchLatest(): Observable<T> = switchMap { it }

fun main() {
    Exercise1().run()
    Exercise3().run()
    Exercise4().run()

    Thread.sleep(1000)
}

/**
Exercise2 Possible solutions:

myList.associate { it.key to it.value }
myList.associateBy({ it.key }, { it.value })
myList.map { Pair(it.key, it.value) }.toMap()
 */