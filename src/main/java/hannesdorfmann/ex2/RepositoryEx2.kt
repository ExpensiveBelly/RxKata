package hannesdorfmann.ex2

import hannesdorfmann.types.PersonWithAddress
import hannesdorfmann.types.View
import io.reactivex.Observable

internal class RepositoryEx2(private val view: View, private val backendEx2: BackendEx2) {

    fun loadPersons(): Observable<PersonWithAddress> {

        /**
         * From the view layer a user of our app can click on a person to load the details and address about this person.
         */
        throw NotImplementedError()
    }

}
