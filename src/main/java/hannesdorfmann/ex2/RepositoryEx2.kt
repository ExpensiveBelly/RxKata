package hannesdorfmann.ex2

import hannesdorfmann.types.Address
import hannesdorfmann.types.Person
import hannesdorfmann.types.PersonWithAddress
import hannesdorfmann.types.View
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

internal class RepositoryEx2(private val view: View, private val backendEx2: BackendEx2) {

    fun loadPersons(): Observable<PersonWithAddress> =
            view.onPersonClicked()
                    .flatMap {
                        Observable.zip(backendEx2.loadPerson(it),
                                backendEx2.loadAddress(it),
                                BiFunction<Person, Address, PersonWithAddress>
                                { person, address -> PersonWithAddress(person, address) }
                        ).subscribeOn(Schedulers.io())
                    }

}
