package playground.maybe

import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit

class MaybeToSingleTest {

    private val maybeToSingle = MaybeToSingle()

    @Test
    fun `should throw IllegalArgumentException because the sequence contains more than one element due to the repeat`() {
        maybeToSingle.single.test().assertError(IllegalArgumentException::class.java)
    }

    @Test
    fun `should reset the counter in the interval operator when the subjectStop emits`() {
        val testScheduler = TestScheduler()
        val testObserver = maybeToSingle.observableFun(testScheduler).test()

        maybeToSingle.subject.onNext(Unit)

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        testObserver.assertValue(0)

        maybeToSingle.subjectStop.onNext(Unit)

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        testObserver.assertValues(0, 0) //Re-subscription because of the `repeat`

        testScheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS)
        testObserver.assertValues(0, 0, 1)
    }
}