package operators

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import operators.types.Store
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import java.math.BigDecimal

class GroceriesExercise {

    private val store = Store()

    /**
     * Given a list of groceries specified in the `groceries` Observable group all the items in a way that
     * no duplicate calls are made to the `Store` trying to purchase the same item. Calculate the total price of the shopping
     * list using the `purchase` method in `Store` class.
     */

    fun exercisePurchaseGroceries(groceries: Observable<String>): Observable<BigDecimal> =
            groceries
                    .groupBy { it }
                    .flatMap { key ->
                        key.count()
                                .toObservable()
                                .flatMap {
                                    store.purchase(key.key!!, it.toInt())
                                            .subscribeOn(Schedulers.io())
                                }
                    }
                    .reduce(BigDecimal.ZERO, { acc, num -> acc + num })
                    .toObservable()
}




