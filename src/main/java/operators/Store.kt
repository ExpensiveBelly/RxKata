package operators

import io.reactivex.Observable
import java.math.BigDecimal
import java.util.*

internal class Store {

    private val productsCache: MutableSet<String> = Collections.synchronizedSet(HashSet())
    private val pricesMap = hashMapOf("bread" to 10,
            "butter" to 20, "egg" to 30, "milk" to 40, "tomato" to 40, "cheese" to 50)

    /**
     * TODO: Refactor this into idiomatic Kotlin/Rx
     */

    fun purchase(productName: String, quantity: Int): Observable<BigDecimal> {
        return Observable.fromCallable({
            Thread.sleep(1000)
            if (productsCache.contains(productName)) {
                throw Throwable("Products should be grouped")
            }
            println("Purchasing " + productName + ", Quantity: " + quantity + " thread: " + Thread.currentThread().name)
            productsCache.add(productName)
            return@fromCallable pricesMap[productName]?.times(quantity)?.let { BigDecimal(it) }
        })

    }
}
