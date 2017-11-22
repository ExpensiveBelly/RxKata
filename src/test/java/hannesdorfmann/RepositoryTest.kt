package com.hannesdorfmann.ex1

import hannesdorfmann.Person
import hannesdorfmann.Repository
import io.reactivex.observers.TestObserver
import org.junit.Test

class RepositoryTest {

    @Test
    fun loadPersons(){

        val subscriber = TestObserver<List<Person>>()
        val repo = Repository(TestView(), TestBackend())
        repo.loadPersons().subscribeWith(subscriber)

        Thread.sleep(1000)
        subscriber.assertComplete()
        subscriber.assertNoErrors()
        subscriber.assertValue(TestBackend.DATA)
    }

}