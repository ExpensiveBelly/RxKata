package marcinmoskala

import org.amshove.kluent.shouldEqual
import org.junit.Test

class KotlinAcademyTest {

    @Test
    fun group_by_is_the_opposite_of_flatmap() {
        val names = listOf("John", "Simon", "Andrew", "Peter")

        names.groupBy { it.first() }.flatMap { it.value } shouldEqual names
    }

    @Test
    fun multiple_ways_of_doing_fibonacci() {
        fun fibRecursive(n: Int): Int = if (n <= 2) 1 else fibRecursive(n - 1) + fibRecursive(n - 2)

        fun fibIterative(n: Int): Int {
            var a = 1
            var b = 1
            for (i in 3..n) {
                val tmp = a
                a += b
                b = tmp
            }
            return a
        }

        tailrec fun fibTailRec(n: Int, prev: Int, next: Int): Int =
                when (n) {
                    0 -> prev
                    1 -> next
                    else -> fibTailRec(n - 1, next, next + prev)
                }

        fun fibTailRecStart(n: Int) = fibTailRec(n, 0, 1)

        fun fibFunctional(n: Int) = (2 until n).fold(1 to 1) { (f, s), _ -> s to (f + s) }.second

        val testData = mapOf(
                10 to 55,
                20 to 6765,
                30 to 832040,
                40 to 102334155)

        testData.forEach { (n, result) ->
            result shouldEqual
                    fibRecursive(n) shouldEqual
                    fibIterative(n) shouldEqual
                    fibTailRecStart(n) shouldEqual
                    fibFunctional(n)
        }
    }

}