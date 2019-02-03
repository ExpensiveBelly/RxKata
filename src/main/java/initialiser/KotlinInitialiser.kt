package initialiser

/**
 * https://medium.com/keepsafe-engineering/an-in-depth-look-at-kotlins-initializers-a0420fcbf546
 */

open class Parent(arg: Unit = println("Parent primary constructor default argument")) {
    private val a = println("Parent.a")

    init {
        println("Parent primary constructor")
    }

    init {
        println("Parent.init")
    }

    private val b = println("Parent.b")
}

class Child : Parent {
    val a = println("Child.a")

    init {
        println("Child.init 1")
    }

    constructor(arg: Unit = println("Child primary constructor default argument")) : super() {
        println("Child primary constructor")
    }

    val b = println("Child.b")

    constructor(arg: Int, arg2: Unit = println("Child secondary constructor default argument")) : this() {
        println("Child secondary constructor")
    }

    init {
        println("Child.init 2")
    }
}

/**
 *
 * Child secondary constructor default argument
 * Child primary constructor default argument
 * Parent primary constructor default argument
 * Parent.a
 * Parent.init
 * Parent.b
 * Parent primary constructor
 * Child.a
 * Child.init 1
 * Child.b
 * Child.init 2
 * Child primary constructor
 * Child secondary constructor
 */

fun main() {
    Child(1)
}