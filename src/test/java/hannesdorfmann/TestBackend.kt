package com.hannesdorfmann.ex1

import hannesdorfmann.Backend
import hannesdorfmann.Person
import io.reactivex.Observable

class TestBackend : Backend {
    companion object {
        val DATA = listOf(Person("Franz", "Beckenbauer"), Person("Mats", "Hummels"))
    }

    override fun loadPersons(): Observable<List<Person>> = Observable.fromCallable {
        println("Loading Persons from backend ...")
        Thread.sleep(500)
        DATA
    }
}