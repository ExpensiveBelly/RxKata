package operators

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import operators.types.Store
import org.javatuples.Pair
import java.math.BigDecimal

class GroceriesSolutions {

    private val store = Store()

    /**
     * Given a list of groceries specified in the `groceries` Observable group all the items in a way that
     * no duplicate calls are made to the `Store` trying to purchase the same item. Calculate the total price of the shopping
     * list using the `purchase` method in `Store` class.
     */

    fun exercisePurchaseGroceries(groceries: Observable<String>): Observable<BigDecimal> {
//        return groceries
//                .flatMap { product ->
//                    store.purchase(product, 1)
//                }
//                .reduce({ obj, augend -> obj.add(augend) })
//                .toObservable()

        return groceries
                .groupBy { s -> s }
                .flatMap { grouped ->
                    grouped
                            .count()
                            .map { quantity -> Pair<String, Long>(grouped.key, quantity) }
                            .toObservable()
                }
                .flatMap { pair ->
                    store.purchase(pair.value0, pair.value1.toInt())
                            .subscribeOn(Schedulers.io())
                }
                .reduce { obj, augend -> obj.add(augend) }
                .toObservable()
    }
}
