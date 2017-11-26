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
        println("Purchasing " + productName + ", Quantity: " + quantity + " thread: " + Thread.currentThread().name)
        if (productsCache.contains(productName)) {
            return Observable.error(Throwable("Products should be grouped"))
        }
        productsCache.add(productName)
        return Observable.just(pricesMap[productName]?.times(quantity)?.let { BigDecimal(it) })
    }
}
