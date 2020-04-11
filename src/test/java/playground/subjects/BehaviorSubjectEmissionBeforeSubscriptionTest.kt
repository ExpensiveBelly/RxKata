package playground.subjects

import io.reactivex.rxjava3.subjects.BehaviorSubject
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class BehaviorSubjectEmissionBeforeSubscriptionTest {

    @Test
    fun `should emit last emission regardless of whether there's a subscriber or not`() {
        val subject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

        subject.onNext(true)

        assertFalse(subject.hasObservers())

        subject.test().assertValue(true)
    }

    @Test
    fun `should emit last emission for multiple subscribers`() {
        val subject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

        subject.test().assertValue(false)

        subject.onNext(true)

        assertTrue(subject.hasObservers())

        subject.test().assertValue(true)
    }
}