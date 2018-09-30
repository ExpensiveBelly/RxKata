package playground

import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.TimeUnit


/**
 * https://github.com/ReactiveX/RxJava/wiki/Observable-Utility-Operators
 */

class DelaySubscriptionExercise {

    fun delay_subscription(testScheduler: TestScheduler) = Observable
            .range(1, 3)
            .delaySubscription(1, TimeUnit.SECONDS, testScheduler)

    fun delay_subscription_till_an_observable_emits(testScheduler: TestScheduler) =
            Observable
                    .range(1, 3)
                    .delaySubscription(Observable.empty<Any>().delay(1, TimeUnit.SECONDS, testScheduler))

    fun delay_subscription_till_an_observable_emits(delayer: Observable<Any>) =
            Observable
                    .range(1, 3)
                    .delaySubscription(delayer)
}