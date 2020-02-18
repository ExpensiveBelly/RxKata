package operators

import io.reactivex.Observable
import io.reactivex.observables.GroupedObservable
import io.reactivex.schedulers.Schedulers
import operators.types.Store

class GroceriesSolutions {

    private val store = Store()

    /**
     * Given a list of groceries specified in the `groceries` Observable group all the items in a way that
     * no duplicate calls are made to the `Store` trying to purchase the same item. Calculate the total price of the shopping
     * list using the `purchase` method in `Store` class.
     */

    fun exercisePurchaseGroceries(groceries: Observable<String>) = groceries
        .groupBy { s -> s }
        .flatMapSingle { grouped: GroupedObservable<String, String> ->
            grouped.count().map { quantity -> Pair(grouped.key, quantity) }
        }
        .flatMap { pair ->
            pair.first?.let {
                store.purchase(it, pair.second.toInt())
                    .subscribeOn(Schedulers.io())
            }
        }
        .reduce { obj, augend -> obj.add(augend) }
        .toObservable()
}
