package delegation

/**
 * Slack discussion about Delegation
 */

class Delegation {
    interface A {
        fun foo()
    }

    interface B {
        fun bar()
    }

    class AImpl(private val b: B) : A {
        override fun foo() {
            b.bar()
        }
    }

    class BImpl : B {
        override fun bar() {}
    }

    interface C : A, B
    //What I'm doing
    class CImpl2() : B by BImpl(), A {
        private val a: A by lazy { AImpl(this) }
        override fun foo() {
            a.foo()
        }
    }

    //What I would like to do
//    class CImpl() : B by BImpl(), A by AImpl(this) //Doesn't compile. Why? `this` can't be used in the constructor context

    //Solution:
    class CImpl(b: B = BImpl(), a: A = AImpl(b)) : B by b, A by a

}
