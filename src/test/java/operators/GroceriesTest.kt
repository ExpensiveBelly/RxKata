package operators

import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class GroceriesTest {

    private lateinit var groceries: Groceries

    @Before
    @Throws(Exception::class)
    fun setUp() {
        groceries = Groceries()
    }

    @Test
    fun groceriesExercise() {
        val test = groceries.exercisePurchaseGroceries(Observable
                .just("bread", "butter", "egg", "milk", "tomato",
                        "cheese", "tomato", "egg", "egg"))
                .test()
        test.awaitTerminalEvent()
        test.assertResult(BigDecimal(290))

    }
}