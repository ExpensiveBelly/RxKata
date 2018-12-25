package marcinmoskala

import org.junit.Test
import kotlin.test.assertEquals

class AwesomeFunctionalKtTest {

    @Test
    fun print_pascal() {
        pascal().take(10).forEach(::println)
    }

    @Test
    fun should_sort() {
        assertEquals(listOf("D", "B", "C", "A").quickSort(), listOf("A", "B", "C", "D"))
    }

    @Test
    fun all_fib_are_the_same() {
        val n = 10
        assert(setOf(fib1(n), fib2(n), fib3(n), fib4(n)).size == 1)
    }
}