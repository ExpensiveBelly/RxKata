package algorithm

/**
 * ARRAYS
 */

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

/**
 * Sudoku is a number-placement puzzle. The objective is to fill a 9 × 9 grid with numbers in such a way that each column,
 * each row, and each of the nine 3 × 3 sub-grids that compose the grid all contain all of the numbers from 1 to 9 one time.
 *
 * Implement an algorithm that will check whether the given grid of numbers represents a valid Sudoku puzzle according to
 * the layout rules described above. Note that the puzzle represented by grid does not have to be solvable.
 */

fun sudoku2(grid: MutableList<MutableList<Char>>): Boolean {
//    val chunked: List<List<MutableList<Char>>> = grid.chunked(3)
//    printMatrix(grid)
//    println()
    val chunked = grid.map { it.chunked(3) }
    val chunked2 = (0 until 3).map { x ->
        (0 until 9).map { y ->
            chunked[y][x]
        }.chunked(3)
    }.flatten()

//    printMatrix(chunked2, "chunked2")
//    val duplicateThreeGrid = chunked2.map { it.map { it.map { it.groupingBy { it }.eachCount().also { println(it) }.filterNot { it.key == '.' } } }}.map { it.any { it.any { it.values.any { it > 1 } } } }.any { it }

    val threeByThreeGridDuplicate = chunked2.map { it.flatten().groupingBy { it }.eachCount().filterNot { it.key == '.' } }.any { it.values.any { it > 1 } }
//    val threeByThreeGridDuplicate = chunked2.map { it.map { it.map { it.groupingBy { it }.eachCount().filterNot { it.key == '.' } } }.map { it.any { it.values.any { it > 1 } } }.any { it } }.any { it }
    val rowDuplicate = grid.map { it.groupBy { it }.filterNot { it.key == '.' }.any { it.value.size > 1 } }.any { it }
    val columnDuplicate = (0 until 9).map { x ->
        (0 until grid.size).map { y -> grid[y][x] }
    }.chunked(9).map { it.map { it.groupBy { it }.filterNot { it.key == '.' }.any { it.value.size > 1 } } }.any { it.any { it } }

    val value = !(threeByThreeGridDuplicate || rowDuplicate || columnDuplicate)
    println(value)
    return value
}

/**
 * A cryptarithm is a mathematical puzzle for which the goal is to find the correspondence between letters and digits,
 * such that the given arithmetic equation consisting of letters holds true when the letters are converted to digits.
 *
 * You have an array of strings crypt, the cryptarithm, and an an array containing the mapping of letters and digits,
 * solution. The array crypt will contain three non-empty strings that follow the
 * structure: [word1, word2, word3], which should be interpreted as the word1 + word2 = word3 cryptarithm.
 *
 * If crypt, when it is decoded by replacing all of the letters in the cryptarithm with digits using the mapping in
 * solution, becomes a valid arithmetic equation containing no numbers with leading zeroes, the answer is true.
 * If it does not become a valid arithmetic solution, the answer is false.
 *
 * Note that number 0 doesn't contain leading zeroes (while for example 00 or 0123 do).
 */

fun isCryptSolution(crypt: MutableList<String>, solution: MutableList<MutableList<Char>>): Boolean {
    val mapping = solution.associate { it.first() to it.last() }

    return try {
        val list = crypt.map {
            val size = it.toCharArray().size
            it.toCharArray().map { mapping[it].toString() }.let {
                if (it.size > 1 && it.first().startsWith("0")) {
                    return false
                }
                it
            }.mapIndexed { index, c -> c.toInt() * Math.pow(10.toDouble(), (size - 1 - index).toDouble()).toInt() }.sum()
        }

        return list[0] + list[1] == list[2]
    } catch (e: NumberFormatException) {
        false
    }
}


fun main() {
//    println(isCryptSolution(mutableListOf("SEND", "MORE", "MONEY"), mutableListOf(
//            mutableListOf('O', '0'),
//            mutableListOf('M', '1'),
//            mutableListOf('Y', '2'),
//            mutableListOf('E', '5'),
//            mutableListOf('N', '6'),
//            mutableListOf('D', '7'),
//            mutableListOf('R', '8'),
//            mutableListOf('S', '9'))))
//
//    println(isCryptSolution(mutableListOf("TEN", "TWO", "ONE"), mutableListOf(
//            mutableListOf('O', '1'),
//            mutableListOf('T', '0'),
//            mutableListOf('W', '9'),
//            mutableListOf('E', '5'),
//            mutableListOf('N', '4'))))

//    ],
//            [],
//            [],
//            [],
//            [

//    println(firstDuplicateInefficient(mutableListOf(2, 2)))
//    val a = mutableListOf(
//            (1..6).toMutableList(),
//            (7..12).toMutableList(),
//            (13..18).toMutableList(),
//            (19..24).toMutableList(),
//            (25..30).toMutableList(),
//            (31..36).toMutableList())
//
//    printMatrix(a, 'original')
//    printMatrix(rotateImage(a))
    val validSudoku = mutableListOf(
            mutableListOf('.', '.', '.', '1', '4', '.', '.', '2', '.'),
            mutableListOf('.', '.', '6', '.', '.', '.', '.', '.', '.'),
            mutableListOf('.', '.', '.', '.', '.', '.', '.', '.', '.'),
            mutableListOf('.', '.', '1', '.', '.', '.', '.', '.', '.'),
            mutableListOf('.', '6', '7', '.', '.', '.', '.', '.', '9'),
            mutableListOf('.', '.', '.', '.', '.', '.', '8', '1', '.'),
            mutableListOf('.', '3', '.', '.', '.', '.', '.', '.', '6'),
            mutableListOf('.', '.', '.', '.', '.', '7', '.', '.', '.'),
            mutableListOf('.', '.', '.', '5', '.', '.', '.', '7', '.'))

    val invalidSudoku = mutableListOf(
            mutableListOf('.', '.', '.', '.', '2', '.', '.', '9', '.'),
            mutableListOf('.', '.', '.', '.', '6', '.', '.', '.', '.'),
            mutableListOf('7', '1', '.', '.', '7', '5', '.', '.', '.'),
            mutableListOf('.', '7', '.', '.', '.', '.', '.', '.', '.'),
            mutableListOf('.', '.', '.', '.', '8', '3', '.', '.', '.'),
            mutableListOf('.', '.', '8', '.', '.', '7', '.', '6', '.'),
            mutableListOf('.', '.', '.', '.', '.', '2', '.', '.', '.'),
            mutableListOf('.', '1', '.', '2', '.', '.', '.', '.', '.'),
            mutableListOf('.', '2', '.', '.', '3', '.', '.', '.', '.'))

    val invalidSudoku2 = mutableListOf(
            mutableListOf('.', '.', '.', '.', '.', '.', '5', '.', '.'),
            mutableListOf('.', '.', '.', '.', '.', '.', '.', '.', '.'),
            mutableListOf('.', '.', '.', '.', '.', '.', '.', '.', '.'),
            mutableListOf('9', '3', '.', '.', '2', '.', '4', '.', '.'),
            mutableListOf('.', '.', '7', '.', '.', '.', '3', '.', '.'),
            mutableListOf('.', '.', '.', '.', '.', '.', '.', '.', '.'),
            mutableListOf('.', '.', '.', '3', '4', '.', '.', '.', '.'),
            mutableListOf('.', '.', '.', '.', '.', '3', '.', '.', '.'),
            mutableListOf('.', '.', '.', '.', '.', '5', '2', '.', '.'))

    sudoku2(invalidSudoku2)
}