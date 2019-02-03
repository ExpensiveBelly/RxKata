package marcinmoskala


fun <T : Comparable<T>> List<T>.quickSort(): List<T> {
    if (this.size <= 1) return this
    val pivot = this.first()
    val (smaller, greater) = this.drop(1).partition { it <= pivot }
    return smaller.quickSort() + pivot + greater.quickSort()
}

fun pascal() = generateSequence(listOf(1)) { prev ->
    listOf(1) + (1..prev.lastIndex).map { prev[it - 1] + prev[it] } + listOf(1)
}

fun fib1(n: Int): Int = if (n <= 2) 1 else fib1(n - 1) + fib1(n - 2)

fun fib2(n: Int): Int {
    var l = 1
    var lm1 = 1
    for (i in 3..n) {
        val temp = l
        l += lm1
        lm1 = temp
    }
    return l
}

fun fib3(n: Int) = fib3(n, 1, 1)
tailrec fun fib3(n: Int, lm1: Int, l: Int): Int = if (n <= 2) l else fib3(n - 1, l, lm1 + l)

fun fib4(n: Int): Int {
    return (2 until n).fold(1 to 1) { (f, s), _ -> s to (f + s) }.second
}


/**
 * https://medium.com/@hadiyarajesh/power-of-kotlin-generate-fibonacci-series-in-6-lines-of-code-with-lambdas-and-higher-order-91b85998cab7?__s=cpsr5vbwafommpowd9io
 */
val fibonacciSequence = generateSequence(1 to 1) {
    //Generating Sequence starting from 1 {
    it.second to it.first + it.second //Calculating Fn  = Fn-1 + Fn-2 and mapping to return Pair<Int,Int>
}.map { it.second } //Getting first element of next sequence

fun main() {
    println("First X Fibonacci numbers are : 0,1, ${fibonacciSequence.take(20).joinToString()}")
}
