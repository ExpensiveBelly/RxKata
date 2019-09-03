package algorithm.diffutil

import algorithm.diffutil.callback.DefaultDiffUtilCallback
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DiffUtilCalculatorKtTest {

    @Test
    fun should_match_the_snake_for_empty_list() {
        val snake = DiffUtil.Snake().apply {
            x = 0
            y = 0
            size = 0
            removal = false
            reverse = false
        }
        assertEquals(listOf(snake), calculateDiffResult(DefaultDiffUtilCallback(emptyList<String>(), emptyList<String>(), EqualsDiffCallback())).snakes)
    }

    @Test
    fun should_match_the_snake_for_insertion() {
        val snake = DiffUtil.Snake().apply {
            x = 0
            y = 0
            size = 1
            removal = false
            reverse = true
        }
        assertEquals(listOf(snake), calculateDiffResult(DefaultDiffUtilCallback(listOf("Hello"), listOf("Hello", "World"), EqualsDiffCallback())).snakes)
    }

    @Test
    fun should_match_the_snake_for_deletion() {
        val snake = DiffUtil.Snake().apply {
            x = 0
            y = 0
            size = 1
            removal = false
            reverse = true
        }
        assertEquals(listOf(snake), calculateDiffResult(DefaultDiffUtilCallback(listOf("Hello", "World"), listOf("Hello"), EqualsDiffCallback())).snakes)
    }
}