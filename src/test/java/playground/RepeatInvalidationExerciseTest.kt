package playground

import org.junit.Test

class RepeatInvalidationExerciseTest {

    private val repeatRetryInvalidationExercise = RepeatInvalidationExercise()

    @Test
    fun `should resubscribe if the maybe completes`() {
        val testObserver = repeatRetryInvalidationExercise.invalidateRepeatInMaybe(true).test()

        testObserver.assertError(NoSuchElementException::class.java)
    }
}