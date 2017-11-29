package hannesdorfmann.ex2

import hannesdorfmann.types.PersonWithAddress
import hannesdorfmann.types.View
import io.reactivex.Observable
import hannesdorfmann.Address
import hannesdorfmann.Person
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

internal class RepositoryEx2(private val view: View, private val backendEx2: BackendEx2) {

    fun loadPersons(): Observable<PersonWithAddress> {

        /**
         * From the view layer a user of our app can click on a person to load the details and address about this person.
         */
        return view.onPersonClicked().concatMap { integer ->
            Observable.zip<Address, Person, PersonWithAddress>(
                    backendEx2.loadAddress(integer).subscribeOn(Schedulers.io()),
                    backendEx2.loadPerson(integer).subscribeOn(Schedulers.io()),
                    BiFunction { address, person -> PersonWithAddress(person, address) })
        }
    }

}
