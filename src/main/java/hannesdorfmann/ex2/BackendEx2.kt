package hannesdorfmann.ex2

import hannesdorfmann.types.Address
import hannesdorfmann.types.Person
import io.reactivex.Observable

internal interface BackendEx2 {
    fun loadPerson(personId: Int): Observable<Person>
    fun loadAddress(personId: Int): Observable<Address>
}
