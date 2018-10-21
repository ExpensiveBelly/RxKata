package hotelbooking

import kotlin.math.absoluteValue

class HotelBooking {

    fun hotel(arrivals: List<Int>, departures: List<Int>, k: Int): Boolean {
        arrivals.plus(
                departures
                        .map { -it }
                        .toList())
                .sortedBy { it.absoluteValue }
                .map { if (it > 0) 1 else -1 }.toList()
                .fold(0) { f, s ->
                    val sum = f + s
                    if (sum > k) return false else sum
                }
        return true
    }
}
