package algorithm

import org.junit.Assert.assertEquals
import org.junit.Test

class ListExercisesTest {

    private val listExercises = ListExercises()

    /*
        For l = [1, 2, 3, 4, 5] and k = 2, the output should be
        reverseNodesInKGroups(l, k) = [2, 1, 4, 3, 5];
        For l = [1, 2, 3, 4, 5] and k = 1, the output should be
        reverseNodesInKGroups(l, k) = [1, 2, 3, 4, 5];
        For l = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11] and k = 3, the output should be
        reverseNodesInKGroups(l, k) = [3, 2, 1, 6, 5, 4, 9, 8, 7, 10, 11].
    */

    @Test
    fun should_reverse_list_in_k_groups() {
        assertEquals(listOf(2, 1, 4, 3, 5), listExercises.reverseNodesInKGroups(listOf(1, 2, 3, 4, 5), 2))
        assertEquals(listOf(3, 2, 1, 6, 5, 4, 9, 8, 7, 10, 11), listExercises.reverseNodesInKGroups(generateSequence(1) { it + 1 }.take(11).toList(), 3))
    }

    /*
        For l = [0, 1, 0], the output should be
        isListPalindrome(l) = true;
        For l = [1, 2, 2, 3], the output should be
        isListPalindrome(l) = false
    */

    @Test
    fun isPalindrome() {
        assertEquals(true, listExercises.isListPalindrome(listOf(0, 1, 0)))
        assertEquals(false, listExercises.isListPalindrome(listOf(1, 2, 2, 3)))
    }

    /*
        For a = [9876, 5432, 1999] and b = [1, 8001], the output should be
        addTwoHugeNumbers(a, b) = [9876, 5434, 0].

        Explanation: 987654321999 + 18001 = 987654340000.

        For a = [123, 4, 5] and b = [100, 100, 100], the output should be
        addTwoHugeNumbers(a, b) = [223, 104, 105].

        Explanation: 12300040005 + 10001000100 = 22301040105.
     */

    @Test
    fun add_two_huge_numbers() {
        assertEquals(listOf(223, 104, 105), listExercises.addTwoHugeNumbers(listOf(123, 4, 5), listOf(100, 100, 100)))
        assertEquals(listOf(9876, 5434, 0), listExercises.addTwoHugeNumbers(listOf(9876, 5432, 1999), listOf(1, 8001)))
    }

    /*
        For l1 = [1, 2, 3] and l2 = [4, 5, 6], the output should be
        mergeTwoLinkedLists(l1, l2) = [1, 2, 3, 4, 5, 6];
        For l1 = [1, 1, 2, 4] and l2 = [0, 3, 5], the output should be
        mergeTwoLinkedLists(l1, l2) = [0, 1, 1, 2, 3, 4, 5].
     */

    @Test
    fun merge_two_linked_lists() {
        assertEquals(listOf(1, 2, 3, 4, 5, 6), listExercises.mergeTwoLinkedLists(listOf(1, 2, 3), listOf(4, 5, 6)))
        assertEquals(listOf(0, 1, 1, 2, 3, 4, 5), listExercises.mergeTwoLinkedLists(listOf(1, 1, 2, 4), listOf(0, 3, 5)))
    }

    /*
        For l = [3, 1, 2, 3, 4, 5] and k = 3, the output should be
        removeKFromList(l, k) = [1, 2, 4, 5];
        For l = [1, 2, 3, 4, 5, 6, 7] and k = 10, the output should be
        removeKFromList(l, k) = [1, 2, 3, 4, 5, 6, 7].
     */

    @Test
    fun remove_k_from_list() {
        assertEquals(listOf(1, 2, 4, 5), listExercises.removeKFromList(listOf(3, 1, 2, 3, 4, 5), 3))
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7), listExercises.removeKFromList(listOf(1, 2, 3, 4, 5, 6, 7), 10))
    }
}