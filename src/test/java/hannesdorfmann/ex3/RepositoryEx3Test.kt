package hannesdorfmann.ex3

import hannesdorfmann.types.PersonWithAddress
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Test

class RepositoryEx3Test {

    @Test
    fun loadPersons() {

        val favs = PublishSubject.create<Set<Int>>()

        val db = object : ContactsDatabase {

            override fun favoriteContacts() =
                    favs.doOnNext { println("Favorite contacts changed: $it") }
        }


        val subscriber = TestObserver<List<PersonWithAddress>>()

        val repo = RepositoryEx3(TestBackendEx3(), db)
        repo.loadFavorites().subscribeWith(subscriber)
        favs.onNext(emptySet())
        Thread.sleep(500)
        favs.onNext(setOf(1))
        Thread.sleep(200)
        favs.onNext(emptySet())
        favs.onNext(setOf(1,2))
        favs.onComplete()

        subscriber.awaitTerminalEvent()

        subscriber.assertComplete()
        subscriber.assertNoErrors()
        Assert.assertEquals(listOf(
                emptyList(),
                listOf( PersonWithAddress(TestBackendEx3.PERSON_DATA[0], TestBackendEx3.ADDRESS_DATA[1]!!)),
                emptyList(),
                listOf(
                PersonWithAddress(TestBackendEx3.PERSON_DATA[0], TestBackendEx3.ADDRESS_DATA[1]!!),
                PersonWithAddress(TestBackendEx3.PERSON_DATA[1], TestBackendEx3.ADDRESS_DATA[2]!!)
        )),
                subscriber.values())

    }

}