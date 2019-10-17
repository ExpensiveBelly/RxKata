package playground

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * Example of a race condition with Rx using a shared variable
 */

class RaceCondition {

    var x = 0

    val timer1 = Observable.interval(500, TimeUnit.MILLISECONDS).doOnNext { x += it.toInt() }
    val timer2 = Observable.interval(500, TimeUnit.MILLISECONDS).doOnNext { if (x < 5) x *= it.toInt() else x += it.toInt() }
}

fun main() {
    RaceCondition().run {
        timer1.subscribe { println("Timer 1: $x") }
        timer2.subscribe { println("Timer 2: $x") }

        Thread.sleep(10000)
    }
}