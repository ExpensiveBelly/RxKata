package hannesdorfmann.ex3

import hannesdorfmann.types.PersonWithAddress
import io.reactivex.Observable

internal class RepositoryEx3(private val backendEx3: BackendEx3, private val contactsDatabase: ContactsDatabase) {


    fun loadFavorites(): Observable<List<PersonWithAddress>> =
            backendEx3.loadAllPersons()
                    .flatMap { people ->
                        contactsDatabase.favoriteContacts()
                                .map { favourites -> people.filter { favourites.contains(it.person.id) } }
                    }
}
