package novoda

import org.junit.Before
import org.junit.Test

class BasicExercisesTest {

    private lateinit var basicExercises: BasicSolutions

    @Before
    fun setUp() {
        basicExercises = BasicSolutions()
    }

    @Test
    fun basic() {
        basicExercises.basicExercise()
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
        basicExercises.infiniteExercise()
                .test()
                .assertResult("0:This is the first sentence 1:I want those to be enumerated 2:How would you ask? 3:That is yours to find out!")
    }
}