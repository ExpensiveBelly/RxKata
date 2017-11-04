package novoda

import org.junit.Before
import org.junit.Test

class BasicExercisesTest {

    private lateinit var basicExercises: BasicExercises

    @Before
    fun setUp() {
        basicExercises = BasicExercises()
    }

    @Test
    fun basic() {
        basicExercises.basicExercise()
                .test()
                .assertResult(
                        "Integer : 0",
                        "Integer : 0",
                        "Integer : 0",
                        "Integer : 2",
                        "Integer : 2",
                        "Integer : 2",
                        "Integer : 4",
                        "Integer : 4",
                        "Integer : 4",
                        "Integer : 6",
                        "Integer : 6",
                        "Integer : 6",
                        "Integer : 8",
                        "Integer : 8",
                        "Integer : 8",
                        "Integer : 10",
                        "Integer : 10",
                        "Integer : 10",
                        "Integer : 12",
                        "Integer : 12",
                        "Integer : 12")
    }
}