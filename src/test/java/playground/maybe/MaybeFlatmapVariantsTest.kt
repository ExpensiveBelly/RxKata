package playground.maybe

import org.junit.Test

class MaybeFlatmapVariantsTest {

    private val maybeFlatmapVariants = MaybeFlatmapVariants()

    @Test
    fun `flatmap single will throw an Exception if the Maybe is empty`() {
        val testObserver = maybeFlatmapVariants.flatMapSingle.test()

        testObserver.assertError(NoSuchElementException::class.java)
    }

    @Test
    fun `flatmap single element will complete if the Maybe is empty`() {
        val testObserver = maybeFlatmapVariants.flatMapSingleElement.test()

        testObserver.assertComplete()
    }
}