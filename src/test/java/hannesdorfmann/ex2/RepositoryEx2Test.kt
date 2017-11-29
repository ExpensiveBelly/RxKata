package hannesdorfmann.ex2

import hannesdorfmann.types.PersonWithAddress
import hannesdorfmann.types.View
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Test
import kotlin.system.measureTimeMillis

class RepositoryEx2Test {

    @Test
    fun loadPersons() {

        val clickPublisher = PublishSubject.create<Int>()

        val testView = object : View {
            override fun onPersonClicked(): Observable<Int> = clickPublisher
                    .doOnNext { println("clicked on Person with id=$it") }
        }
        val subscriber = TestObserver<PersonWithAddress>()

        val repo = RepositoryEx2(testView, TestBackendEx2())
        val duration = measureTimeMillis {
            repo.loadPersons().subscribeWith(subscriber)
            clickPublisher.onNext(2)
            clickPublisher.onNext(1)
            clickPublisher.onComplete()

            subscriber.awaitTerminalEvent()
        }

        subscriber.assertComplete()
        subscriber.assertNoErrors()
        Assert.assertEquals(listOf(
                PersonWithAddress(TestBackendEx2.PERSON_DATA[1], TestBackendEx2.ADDRESS_DATA[2]!!),
                PersonWithAddress(TestBackendEx2.PERSON_DATA[0], TestBackendEx2.ADDRESS_DATA[1]!!)
        ),
                subscriber.values())

        Assert.assertTrue("Overall, loading took to long. Expected that loading 2 Persons + their Addresses take less than 2000 ms. Your implementation took $duration ms. Try to parallize things", duration < 2000)
    }

}