package algorithm

/**
 * Inefficient solution
 *
 * https://app.codesignal.com/interview-practice/task/pMvymcahZ8dY4g75q
 *
 * Given an array a that contains only numbers in the range from 1 to a.length,
 * find the first duplicate number for which the second occurrence has the minimal index.
 * In other words, if there are more than 1 duplicated numbers,
 * return the number for which the second occurrence has a smaller index than the second occurrence
 * of the other number does. If there are no such elements, return -1.
 */

private fun firstDuplicateInefficient(a: MutableList<Int>): Int {
    return a.asSequence()
            .mapIndexed { index, number -> index to number }
            .groupBy { it.second }.also { println("1 $it") }
            .filter { it.value.size > 1 }.also { println("2 $it") }
            .mapValues { it.value.drop(1) }
            .minBy { it.value.first().first }.also { println("3 $it") }
            ?.toPair()?.second?.first()?.second ?: -1
}

private fun firstDuplicate(a: MutableList<Int>): Int {
    val set = mutableSetOf<Int>()
    for (i in a) {
        if (!set.add(i)) {
            return i
        }
    }
    return -1
}

/**
 * Note: Write a solution that only iterates over the string once and uses O(1) additional memory,
 * since this is what you would be asked to do during a real interview.
 *
 * Given a string s, find and return the first instance of a non-repeating character in it.
 * If there is no such character, return '_'.
 */

private fun firstNotRepeatingCharacter(s: String): Char {
    s.toCharArray().forEach {
        if (s.indexOf(it) == s.lastIndexOf(it)) return it
    }
    return '_'
}

/**
 * Note: Try to solve this task in-place (with O(1) additional memory), since this is what you'll be
 * asked to do during an interview.
 *
 * You are given an n x n 2D matrix that represents an image. Rotate the image by 90 degrees (clockwise).
 */

fun rotateImage(a: MutableList<MutableList<Int>>): MutableList<MutableList<Int>> {
    for (x in 0 until a.size / 2) {
        val i = a.size - 1 - x
        for (y in x until i) {
            val temp = a[x][y]
            //top-left = bottom-left
            a[x][y] = a[a.size - 1 - y][x]
            //bottom-left = bottom-right
            a[a.size - 1 - y][x] = a[a.size - 1 - x][a.size - 1 - y]
            //bottom-right = top-right
            a[a.size - 1 - x][a.size - 1 - y] = a[y][a.size - 1 - x]
            //top-right = top-left
            a[y][a.size - 1 - x] = temp
        }
    }
    return a
}

fun main() {
//    println(firstDuplicateInefficient(mutableListOf(2, 2)))
    val a = mutableListOf(
            (1..6).toMutableList(),
            (7..12).toMutableList(),
            (13..18).toMutableList(),
            (19..24).toMutableList(),
            (25..30).toMutableList(),
            (31..36).toMutableList())

    printMatrix(a, "original")
    printMatrix(rotateImage(a))
}