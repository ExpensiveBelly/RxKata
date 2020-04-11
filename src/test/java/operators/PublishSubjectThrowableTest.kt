package operators

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import playground.subjects.PublishSubjectThrowable

class PublishSubjectThrowableTest {

    private val publishSubjectThrowable = PublishSubjectThrowable()

    @Test
    fun `should dispose the publish subject but not error it so it can be reused`() {
        val testObserverCompletable = publishSubjectThrowable.completable.test()
        val testObserverSubject = publishSubjectThrowable.onlyDisposesDoesntErrorPublishSubject.test()

        publishSubjectThrowable.onlyDisposesDoesntErrorPublishSubject.onNext(IllegalStateException())

        assertFalse(testObserverSubject.isTerminated)
        assertTrue(testObserverCompletable.isTerminated)
    }
}