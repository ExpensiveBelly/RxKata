package algorithm


fun <T> printMatrix(matrix: List<List<T>>, tag: String = "") {
    println(tag)
    matrix.forEachIndexed { i, list ->
        list.forEachIndexed { j, c ->
            print("$c ")
        }
        println()
    }
    println()
}