package extensions

import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import java.util.concurrent.TimeUnit

class FlatmapDropExerciseTest {

    @Test
    fun should_not_re_trigger_network_request_upon_second_click_with_flatmap_drop() {
        val testScheduler = TestScheduler()
        FlatmapDropExercise(testScheduler).run {
            val clicks = PublishSubject.create<Unit>()

            val testObserver = clicks.multipleClicksDoesNotTriggerNewRequestWithFlatmapDrop().test()

            clicks.onNext(Unit)
            clicks.onNext(Unit)
            testObserver.assertNoValues()

            testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

            testObserver.assertValue(0)
        }
    }

    @Test
    fun should_not_re_trigger_network_request_upon_second_click_without_flatmapdrop() {
        val testScheduler = TestScheduler()
        FlatmapDropExercise(testScheduler).run {
            val clicks = PublishSubject.create<Unit>()

            val testObserver = clicks.multipleClicksDoesNotTriggerNewRequestWithoutFlatmapDrop().test()

            clicks.onNext(Unit)
            clicks.onNext(Unit)
            testObserver.assertNoValues()

            testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

            testObserver.assertValue(0)
        }
    }
}