package playground.subjects

import io.reactivex.rxjava3.exceptions.MissingBackpressureException
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConcatMapWithSubjectTest {

    private val concatMapWithSubject = ConcatMapWithSubject()

    @Test
    fun `should emit the first item`() {
        with(concatMapWithSubject) {
            val testObserver = observable.test()

            assertTrue { initialSubject.hasObservers() }
            assertFalse { secondSubject.hasObservers() }

            initialSubject.onNext(1)
            testObserver.assertNoValues()
            assertTrue { initialSubject.hasObservers() }
            assertTrue { secondSubject.hasObservers() }

            secondSubject.onNext(2)
            testObserver.assertValue(2)
        }
    }

    @Test
    fun `should not emit more items because the second subject has not completed`() {
        with(concatMapWithSubject) {
            val testObserver = observable.test()

            initialSubject.onNext(1)
            testObserver.assertNoValues()
            initialSubject.onNext(2)
            testObserver.assertNoValues()
            initialSubject.onNext(3)
            testObserver.assertNoValues()

            secondSubject.onNext(2)
            testObserver.assertValue(2)
        }
    }

    @Test
    fun `should throw backpressure exception because the buffer limit will be exceeded`() {
        with(concatMapWithSubject) {
            val testScheduler = TestScheduler()
            val testObserver = flowableIntervalFun(testScheduler).test()
            testScheduler.advanceTimeBy(50, TimeUnit.MILLISECONDS)
            testObserver.assertValue(0)
            testScheduler.advanceTimeBy(50, TimeUnit.MILLISECONDS)
            testObserver.assertError(MissingBackpressureException::class.java)
        }
    }
}