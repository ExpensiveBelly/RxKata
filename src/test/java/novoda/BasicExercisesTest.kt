package novoda

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
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test.assertValueAt(0, 1L)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test.assertValueAt(1, 2L)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test.assertValueAt(2, 3L)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test.assertValueAt(3, 4L)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test.assertValueAt(4, 5L)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        test.assertValueAt(5, 6L)
    }
}