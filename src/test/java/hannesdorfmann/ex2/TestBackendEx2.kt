package hannesdorfmann.ex2

import hannesdorfmann.types.Address
import hannesdorfmann.types.Person
import io.reactivex.Observable

class TestBackendEx2 : BackendEx2 {
    companion object {
        internal val PERSON_DATA = listOf(Person(1, "Franz", "Beckenbauer"), Person(2, "Mats", "Hummels"))
        internal val ADDRESS_DATA = mapOf(2 to Address("Kronprinzstraße 2", "Dortmund"), 1 to Address("Kaiserallee 52", "Munich"))
    }

    override fun loadPerson(personId : Int): Observable<Person> = Observable.fromCallable {
        println("BackendEx2: >> Loading Person with id = $personId")
        Thread.sleep(800)
        println("BackendEx2: << Response for Person with id = $personId")
        PERSON_DATA.find { it.id == personId }
    }

    override fun loadAddress(personId : Int): Observable<Address> = Observable.fromCallable {
        println("BackendEx2: >> Loading Address for Person with id = $personId")
        Thread.sleep(800)
        println("BackendEx2: << Response for Address for Person with id = $personId")
        ADDRESS_DATA[personId]!!
    }
}