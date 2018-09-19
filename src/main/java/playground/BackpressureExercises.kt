package playground

import io.reactivex.Observable
import java.util.*
import java.util.concurrent.TimeUnit


class BackpressureExercises {

    val dishesRange = Observable.range(1, 1000000000).map { Dish(it) }
    val dishesInterval = Observable.interval(1, TimeUnit.MILLISECONDS).map { Dish(it.toInt()) }

}

data class Dish(private val id: Int, private val oneKb: ByteArray = ByteArray(1024)) {
    init {
        println("Created: $id")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dish

        if (id != other.id) return false
        if (!Arrays.equals(oneKb, other.oneKb)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + Arrays.hashCode(oneKb)
        return result
    }
}