package playground

import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit


class IntervalTest {

    @Test
    fun interval_operator_is_cold_observable_and_starts_repeating_events_from_zero_for_each_subscriber() {
        val testScheduler = TestScheduler()
        val observable = Observable.interval(500, TimeUnit.MILLISECONDS, testScheduler)

        observable.test().also { testScheduler.advanceTimeBy(5000, TimeUnit.MILLISECONDS) }.assertValues(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

        observable.test().also { testScheduler.advanceTimeBy(2500, TimeUnit.MILLISECONDS) }.assertValues(0, 1, 2, 3, 4)
    }
}