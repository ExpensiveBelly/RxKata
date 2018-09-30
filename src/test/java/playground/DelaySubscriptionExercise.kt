package playground

import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.Test
import java.util.concurrent.TimeUnit

class DelaySubscriptionExerciseTest {

    private val delaySubscriptionExercise: DelaySubscriptionExercise = DelaySubscriptionExercise()
    private val testScheduler = TestScheduler()

    @Test
    fun should_delay_subscription_by_1_second() {
        val testObserver = delaySubscriptionExercise.delay_subscription(testScheduler).test()
        testObserver
                .assertSubscribed()
                .assertNoValues()

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        testObserver.assertValues(1, 2, 3)
    }

    @Test
    fun should_delay_subscription_till_a_delayed_observable_emits() {
        val testObserver = delaySubscriptionExercise.delay_subscription_till_an_observable_emits(testScheduler).test()
        testObserver
                .assertSubscribed()
                .assertNoValues()

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        testObserver.assertValues(1, 2, 3)
    }

    @Test
    fun should_never_emit_any_values_if_the_observable_is_disposed_before_the_delay_time() {
        val testObserver = delaySubscriptionExercise.delay_subscription_till_an_observable_emits(testScheduler).test()
        testObserver
                .assertSubscribed()
                .assertNoValues()

        testObserver.dispose()
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        testObserver.assertNoValues()
    }

    @Test
    fun should_delay_subscription_till_a_custom_observable_emits() {
        val delayer = BehaviorSubject.create<Any>()

        delayer.onNext(Any())
        val testObserver = delaySubscriptionExercise.delay_subscription_till_an_observable_emits(delayer).test()
        testObserver
                .assertSubscribed()
                .assertValues(1, 2, 3)
    }

    @Test
    fun should_delay_till_a_custom_observable_emits() {
        val delayer = Observable.empty<Any>().delay(1, TimeUnit.SECONDS, testScheduler)
        val testObserver = delaySubscriptionExercise.delay_subscription_till_an_observable_emits(delayer).test()
        testObserver
                .assertSubscribed()
                .assertNoValues()

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        testObserver.assertValues(1, 2, 3)
    }
}