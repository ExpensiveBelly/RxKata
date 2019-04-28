package algorithm


class ListFunctionalExercises {
/*
    For l = [1, 2, 3, 4, 5] and k = 2, the output should be
    reverseNodesInKGroups(l, k) = [2, 1, 4, 3, 5];
    For l = [1, 2, 3, 4, 5] and k = 1, the output should be
    reverseNodesInKGroups(l, k) = [1, 2, 3, 4, 5];
    For l = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11] and k = 3, the output should be
    reverseNodesInKGroups(l, k) = [3, 2, 1, 6, 5, 4, 9, 8, 7, 10, 11].
*/

    fun reverseNodesInKGroups(l: List<Int>, k: Int) =
            l.windowed(size = k, step = k, partialWindows = true).map { if (it.size == k) it.asReversed() else it }.flatten()

    /*
For l = [0, 1, 0], the output should be
 isListPalindrome(l) = true;
For l = [1, 2, 2, 3], the output should be
isListPalindrome(l) = false
 */
    fun isListPalindrome(l: List<Int>) = (l == l.asReversed())

/*
For a = [9876, 5432, 1999] and b = [1, 8001], the output should be
addTwoHugeNumbers(a, b) = [9876, 5434, 0].

Explanation: 987654321999 + 18001 = 987654340000.

For a = [123, 4, 5] and b = [100, 100, 100], the output should be
addTwoHugeNumbers(a, b) = [223, 104, 105].

Explanation: 12300040005 + 10001000100 = 22301040105.
 */

    fun addTwoHugeNumbers(a: List<Int>, b: List<Int>): List<Int> {
        return propagateCarry(a.reversed().zipAll(b.reversed(), 0, 0)
                .fold(emptyList()) { acc, pair -> acc + (pair.first + pair.second) })
    }

    private fun propagateCarry(summedList: List<Int>): List<Int> {
        var listWithCarry = emptyList<Int>()

        var carry = 0
        summedList.forEachIndexed { index: Int, _: Int ->
            val element = summedList[index] + carry
            listWithCarry += element % 10000
            carry = (element / 10000)
        }
        if (carry > 0) {
            listWithCarry += carry
        }
        return listWithCarry.reversed()
    }

    private fun <T1 : Any, T2 : Any> List<T1>.zipAll(other: List<T2>, emptyValue: T1, otherEmptyValue: T2): List<Pair<T1, T2>> {
        val i1 = this.iterator()
        val i2 = other.iterator()
        return generateSequence {
            if (i1.hasNext() || i2.hasNext()) {
                Pair(if (i1.hasNext()) i1.next() else emptyValue,
                        if (i2.hasNext()) i2.next() else otherEmptyValue)
            } else {
                null
            }
        }.toList()
    }

    /*
For l1 = [1, 2, 3] and l2 = [4, 5, 6], the output should be
mergeTwoLinkedLists(l1, l2) = [1, 2, 3, 4, 5, 6];
For l1 = [1, 1, 2, 4] and l2 = [0, 3, 5], the output should be
mergeTwoLinkedLists(l1, l2) = [0, 1, 1, 2, 3, 4, 5].
 */
    fun mergeTwoLinkedLists(l1: List<Int>, l2: List<Int>) = (l1 + l2).sorted()

    /*
For l = [3, 1, 2, 3, 4, 5] and k = 3, the output should be
removeKFromList(l, k) = [1, 2, 4, 5];
For l = [1, 2, 3, 4, 5, 6, 7] and k = 10, the output should be
removeKFromList(l, k) = [1, 2, 3, 4, 5, 6, 7].
 */
    fun removeKFromList(l: List<Int>, k: Int) = l.filterNot { it == k }
}