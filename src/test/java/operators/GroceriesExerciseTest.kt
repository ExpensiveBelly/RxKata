package operators

import io.reactivex.Observable
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.system.measureTimeMillis

class GroceriesExerciseTest {

    private lateinit var exercises: GroceriesExercise

    @Before
    @Throws(Exception::class)
    fun setUp() {
        exercises = GroceriesExercise()
    }

    @Test
    fun groceriesExercise() {
        val duration = measureTimeMillis {
            val test = exercises.exercisePurchaseGroceries(Observable
                    .just("bread", "butter", "egg", "milk", "tomato",
                            "cheese", "tomato", "egg", "egg"))
                    .test()
            test.awaitTerminalEvent()
            test.assertResult(BigDecimal(290))
        }
        assertTrue("duration was: $duration. Parallelise more", duration < 1500)
    }
}