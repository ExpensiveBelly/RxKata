package hannesdorfmann.ex4

import hannesdorfmann.Person
import io.reactivex.Observable

interface BackendEx4 {

    fun searchfor(searchFor: String): Observable<List<Person>>
}
