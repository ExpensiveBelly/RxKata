package hannesdorfmann.ex3

import hannesdorfmann.types.PersonWithAddress
import io.reactivex.Observable

internal class RepositoryEx3(private val backendEx3: BackendEx3, private val contactsDatabase: ContactsDatabase) {


    fun loadFavorites(): Observable<List<PersonWithAddress>> {

        /**
         * Provide an observable that only emits a list of PersonWithAddress if they are marked as favorite ones.
         */

        return contactsDatabase.favoriteContacts()
                .flatMap { integers ->
                    backendEx3.loadAllPersons()
                            .flatMap<PersonWithAddress>({ Observable.fromIterable(it) })
                            .filter { (person) -> integers.contains(person.id) }
                            .toList()
                            .toObservable()
                }
    }
}
