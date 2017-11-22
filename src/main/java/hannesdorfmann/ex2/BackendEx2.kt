package hannesdorfmann.ex2

import hannesdorfmann.Person
import hannesdorfmann.Address
import io.reactivex.Observable

internal interface BackendEx2 {
    fun loadPerson(personId: Int): Observable<Person>
    fun loadAddress(personId: Int): Observable<Address>
}
