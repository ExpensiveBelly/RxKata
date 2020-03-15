package coroutines

import junit.framework.TestCase.assertTrue
import org.junit.Test
import kotlin.system.measureNanoTime

class ParallelTest {

    private val n = 100

    @Test
    fun `should paralellise map operation and it should take less time for expensive operations than normal map`() {
        val list = generateSequence(0) { it + 1 }.take(n).toList()

        val parallelTime = measureNanoTime {
            list.pmap { compute(it) }
        }
        val sequentialTime = measureNanoTime {
            list.map { compute(it) }
        }

        println("parallelTimeMillis: $parallelTime")
        println("sequentialTimeMillis: $sequentialTime")

        assertTrue(parallelTime < sequentialTime)
    }

    private fun compute(i: Int): Int {
        Thread.sleep(100)
        return i
    }

    @Test
    fun `should paralellise for loop operation and it should take less time for expensive operations than normal forEach`() {
        val list = generateSequence(0) { it + 1 }.take(n).toList()

        val parallelTime = measureNanoTime {
            list.forEachParallel { compute(it) }
        }
        val sequentialTime = measureNanoTime {
            list.forEach { compute(it) }
        }

        println("parallelTimeMillis: $parallelTime")
        println("sequentialTimeMillis: $sequentialTime")

        assertTrue(parallelTime < sequentialTime)
    }

    @Test
    fun `should paralellise map operation and parallel takes more time than normal map for non-expensive operations`() {
        val list = generateSequence(0) { it + 1 }.take(n).toList()

        val parallelTime = measureNanoTime {
            list.pmap { it * 10 }
        }
        val sequentialTime = measureNanoTime {
            list.map { it * 10 }
        }

        println("parallelTimeMillis: $parallelTime")
        println("sequentialTimeMillis: $sequentialTime")

        assertTrue(parallelTime > sequentialTime)
    }

    @Test
    fun `should paralellise for loop operation and parallel takes more time than normal forEach for non-expensive operations`() {
        val list = generateSequence(0) { it + 1 }.take(n).toList()

        val parallelTime = measureNanoTime {
            list.forEachParallel { it * 10 }
        }
        val sequentialTime = measureNanoTime {
            list.forEach { it * 10 }
        }

        println("parallelTimeMillis: $parallelTime")
        println("sequentialTimeMillis: $sequentialTime")

        assertTrue(parallelTime > sequentialTime)
    }

}