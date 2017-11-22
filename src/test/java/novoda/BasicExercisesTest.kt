package novoda

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit


class BasicExercisesTest {

    private lateinit var exercises: BasicSolutions
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        exercises = BasicSolutions()
        testScheduler = TestScheduler()
    }

    @Test
    fun basic() {
        exercises.basicExercise()
                .test()
                .assertResult(
                        "Integer : 0",
                        "Integer : 0",
                        "Integer : 0",
                        "Integer : 0",
                        "Integer : 2",
                        "Integer : 4",
                        "Integer : 6",
                        "Integer : 8",
                        "Integer : 10",
                        "Integer : 12")
    }

    @Test
    fun infinite() {
        exercises.infiniteExercise()
                .test()
                .assertResult("0:This is the first sentence 1:I want those to be enumerated 2:How would you ask? 3:That is yours to find out!")
    }


    @Test
    fun timer() {
        val test = exercises.timer(testScheduler).test()
        test.assertNoValues()
        advanceTimeAndAssert(test, 1)
        advanceTimeAndAssert(test, 2)
        advanceTimeAndAssert(test, 3)
        advanceTimeAndAssert(test, 4)
        advanceTimeAndAssert(test, 5)
        advanceTimeAndAssert(test, 6)
    }

    private fun advanceTimeAndAssert(test: TestObserver<Long>, valueCount: Int) {
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        test.assertValueCount(valueCount)
        test.assertValueAt(valueCount - 1, valueCount.toLong())
    }

    @Test
    fun count() {
        exercises.count(Observable.empty<Char>()).test().assertResult(0)

        exercises.count(Observable
                .just('A', 'B', 'C', 'D', 'E', 'F')).test().assertResult(6)
    }
}