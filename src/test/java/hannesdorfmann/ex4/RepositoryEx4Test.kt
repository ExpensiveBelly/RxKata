package hannesdorfmann.ex4

import hannesdorfmann.Person
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Test

class RepositoryEx4Test {

    @Test
    fun loadPersons() {

        val search = PublishSubject.create<String>()

        val view = object : ViewEx4 {
            override fun onSearchTextchanged(): Observable<String> = search.doOnNext { println("User input: '$it'") }
        }

        val backend = TestBackendEx4()
        val subscriber = TestObserver<List<Person>>()

        val repo = SearchRepository(view, backend)
        repo.search().subscribeWith(subscriber)

        search.onNext("H")
        search.onNext("Ha")
        Thread.sleep(400)
        search.onNext("H")
        search.onNext("")
        search.onNext("F")
        search.onNext("Fr")
        search.onNext("Fra")
        search.onNext("Fran")
        search.onNext("Franz")
        Thread.sleep(800)
        search.onNext("Fran")
        search.onNext("Fra")
        search.onNext("Fr")
        search.onNext("Fran")
        search.onNext("Fra")
        search.onNext("Franz")
        Thread.sleep(800)
        search.onNext("Franzis")
        Thread.sleep(400)
        search.onNext("Thom")
        Thread.sleep(1000)
        search.onComplete()

        subscriber.awaitTerminalEvent()

        subscriber.assertComplete()
        subscriber.assertNoErrors()

        Assert.assertEquals(listOf("Franz", "Franzis", "Thom"), backend.queries)

        val msg = "Expected searchresults: [" + backend.search("Franz").niceString() + "] and [" + backend.search("Thom").niceString() + "] but got the following search results: " + subscriber.values().niceStringList()
        Assert.assertEquals(
                listOf(backend.search("Franz"),
                        backend.search("Thom")),
                subscriber.values()
        )
    }

    private fun List<Person>.niceString() =
            map { it.toString() }
                    .fold("") { old, new -> old + new }


    private fun List<List<Person>>.niceStringList(): String {
        return map { "[${it.niceString()}] , " }
                .fold("") { old, new -> old + new }
    }

}