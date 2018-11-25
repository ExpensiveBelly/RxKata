package hannesdorfmann.ex3

import hannesdorfmann.types.PersonWithAddress
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

internal class RepositoryEx3(private val backendEx3: BackendEx3, private val contactsDatabase: ContactsDatabase) {


    fun loadFavorites(): Observable<List<PersonWithAddress>> {

        /**
         * Provide an observable that only emits a list of PersonWithAddress if they are marked as favorite ones.
         */

//        Solution 1
//        return contactsDatabase.favoriteContacts()
//                .flatMap { integers ->
//                    backendEx3.loadAllPersons()
//                            .flatMap<PersonWithAddress>({ Observable.fromIterable(it) })
//                            .filter { (person) -> integers.contains(person.id) }
//                            .toList()
//                            .toObservable()
//                }

        // Solution 2
        return Observable.combineLatest(contactsDatabase.favoriteContacts().subscribeOn(Schedulers.io()),
                backendEx3.loadAllPersons().subscribeOn(Schedulers.io()),
                BiFunction { favouriteContactsIds, personsWithAddress ->
                    personsWithAddress.filter { it.person.id in favouriteContactsIds }
                })
    }
}
