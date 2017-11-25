package hannesdorfmann.ex3

import hannesdorfmann.PersonWithAddress
import io.reactivex.Observable

internal interface BackendEx3 {
    fun loadAllPersons(): Observable<List<PersonWithAddress>>
}
