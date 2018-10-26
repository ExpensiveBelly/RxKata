package puzzler

/**
 * https://www.youtube.com/watch?v=Xq9vBZs0j-8
 *
 * Explanations below the exercises. For more information and exercises: https://github.com/angryziber/kotlin-puzzlers
 */
class KotlinPuzzlersPart2 {

    class Puzzle1 {

        fun hello(): Boolean {
            println(print("Hello") == print("World") == return false)
        }

        fun execute() {
            hello()
        }
    }

/*
 * Explanation: The return type of return expression is Nothing, and nothing can be assigned to
 * anything, and we are assigning to a  Boolean. return false finish the program.
 */


// =================================================

    class Puzzle2 {
        fun printInt(n: Int) {
            println(n)
        }

        fun execute() {
//        printInt(-2_147_483_648.inc())
        }
    }

/*
Explanation: extension functions have a precedence over unary operator.
By adding parenthesis we fix it: printInt((-2_147_483_648).inc())
 */

// =================================================

//class Puzzle3 {
//
//    var x: UInt = 0u
//    println(x--.toInt())
//    println(--x)
//}

/*
Kotlin 1.3
 */

// =================================================

    class Puzzle4 {

        val cells = arrayOf(
                arrayOf(1, 1, 1),
                arrayOf(0, 1, 1),
                arrayOf(1, 0, 1))

        val neighbours =
                cells[0][0] + cells[0][1] + cells[0][2]
//                    + cells[1][0] + cells[1][2]
//                    + cells[2][0] + cells[2][1] + cells[2][2]

        fun execute() {
            print(neighbours)
        }
    }

/*
Explanation: this is the problem with not having semi-colons. Kotlin doesn't know that we
are continuing next line, the solutions is to have the + at the end of the line so Kotlin
knows that we're done yet.

cells[0][0] + cells[0][1] + cells[0][2] +
cells[1][0] + cells[1][2] +
cells[2][0] + cells[2][1] + cells[2][2]
 */

    // =================================================
    class Puzzle5 {

        val x: Int? = 2
        val y: Int = 3

        val sum = (x ?: 0) + y

        fun execute() {
            print(sum)
        }
    }

/*
Explanation: Parenthesis would solve this problem. If x is not null, only the left side of
the expression is considered. (x ?: 0) + y would fix it and print 5
 */

// =================================================

    class Puzzle6 {

        data class Recipe(var name: String? = null, var hops: List<Hops> = mutableListOf())
        data class Hops(var kind: String? = null, var atMinute: Int = 0, var grams: Int = 0)

        fun beer(build: Recipe.() -> Unit) = Recipe().apply(build)
        fun Recipe.hops(build: Hops.() -> Unit) {
            hops += Hops().apply(build)
        }

        val recipe = beer {
            name = "Simple IPA"

            hops {
                name = "Cascade"
                grams = 100
                atMinute
            }
        }

        fun execute() {
            print(recipe)
        }
    }

    /*
    Explanation: inner-scope builders see the outer scope by default. To fix it use
    @DslMarker annotation class BeerLang
     */

// =================================================

    class Puzzle7 {
        fun f(x: Boolean) {
            when (x) {
                x == true -> println("$x TRUE")
                x == false -> println("$x FALSE")
            }
        }

        fun execute() {
            f(true)
            f(false)
        }
    }

    /*
    Explanation: x will be evaluated in the when and then inside the when as well.
     */

    // =================================================

    class Puzzle8 {
        abstract class NullSafeLang {
            abstract val name: String
            val logo = name[0].toUpperCase()

        }

        class Kotlin : NullSafeLang() {
            override val name = "Kotlin"
        }

        fun execute() {
            println(Kotlin().logo)
        }
    }


    /*
Explanation: logo is not initialised "lazily" but eagerly and by the time it is initialised name is null. The fix is
val logo: Char
    get() = name[0].toUpperCase()

     */

    // =================================================

    class Puzzle9 {

        fun execute() {
            val result = mutableListOf<() -> Unit>()
            var i = 0
            for (j in 1..3) {
                i++
                result += { print("$i $j; ") }
            }

            result.forEach { it() }
        }
    }

    /*
    Explanation: j is like val j, it's passed by value to the lambda. i is a var and it's passed by reference. Inside the for loop
    we only capture the values and references and then we execute in the `forEach`
     */

    // =================================================

    class Puzzle10 {

        fun foo(a: Boolean, b: Boolean) = print("$a, $b")

        val a = 1
        val b = 2
        val c = 3
        val d = 4

        fun execute() {
//            foo(c < a, b > d)
        }
    }

    /*
    Explanation: Why it does not compile? Because it thinks a and b are type parameters of c: c<a,b>
    The fix is to use parenthesis foo(c < a, (b > d))
     */

    // =================================================

    class Puzzle11 {

        data class Container(val name: String, private val items: List<Int>) : List<Int> by items

        fun execute() {
            val (name, items) = Container("Kotlin", listOf(1, 2, 3))
            println("Hello $name, $items")
        }
    }

    /*
    Explanation: destructuring gets component1 and component2. Component2 is a list (all Kotlin collections support destructuring). However,
    in the case of component1 the name is delegated to the data class which is good, but in the case of the list because it's private in the
    data class it's delegated to items (delegation) and it takes component2 of the list itself.

    DON'T DO THIS KIND OF DELEGATION
     */

    // =================================================

    class Puzzle12 {
        fun <T> Any?.asGeneric() = this as? T

        fun execute() {
            42.asGeneric<Nothing>()!!!!

            val a = if (true) 87
            println(a)
        }
    }

    /*
    Explanation: Once Nothing is found then the rest is always unreachable code
     */

    // =================================================

    class Puzzle13 {

        open class A(val x: Any?) {
            override fun toString() = javaClass.simpleName
        }

        object B : A(C)
        object C : A(B)

        fun execute() {
            println(B.x)
            println(C.x)
        }
    }

    /*
    Explanation: the compiler does optimisations when it creates Objects. This code should not compile. The only reason
    it compiles is because it's Any?, if it was non-nullable it wouldn't compile.
     */
    class Puzzle14 {

        val whatAmI = { -> }.fun
                Function<*>.() {}()

        fun execute() {
            println(whatAmI)
        }
    }
}
