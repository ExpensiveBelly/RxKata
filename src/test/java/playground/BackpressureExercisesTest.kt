package playground

import io.reactivex.schedulers.Schedulers
import org.junit.Ignore

class BackpressureExercisesTest {

    private val backpressureExercises: BackpressureExercises = BackpressureExercises()

    @Ignore
    fun range_on_same_thread_should_not_throw_backpressure_error() {
        backpressureExercises.dishesRange.subscribe {
            System.out.println("Range Washing: $it")
            Thread.sleep(50)
        }
    }

    @Ignore
    fun range_on_io_thread_should_not_throw_backpressure_exception() {
        backpressureExercises.dishesRange.observeOn(Schedulers.io()).blockingSubscribe {
            System.out.println("Range with concurrency Washing: $it")
            Thread.sleep(50)
        }
    }

    @Ignore
    fun interval_should_throw_backpressure_exception() {
        backpressureExercises.dishesInterval.observeOn(Schedulers.newThread()).blockingSubscribe {
            System.out.println("Interval Washing: $it")
            Thread.sleep(150000)
        }
    }
}