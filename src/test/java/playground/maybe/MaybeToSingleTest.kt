package playground.maybe

import org.junit.Test

class MaybeToSingleTest {

    private val maybeToSingle = MaybeToSingle()

    @Test
    fun `should throw IllegalArgumentException sequence contains more than one element because of the repeat`() {
        maybeToSingle.single.test().assertError(IllegalArgumentException::class.java)
    }
}