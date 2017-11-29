package hannesdorfmann.ex4

import hannesdorfmann.types.Person
import io.reactivex.Observable

class RepositoryEx4(private val viewEx4: ViewEx4, private val backendEx4: BackendEx4) {


    fun search(): Observable<List<Person>> {

        /**
         * Implement the search call according the following criteria:
         * - The search query string must contain at least 3 characters
         * - To save backend traffic: only search if search query hasn't changed within the last 300 ms
         * - If the user is typing fast "Hannes" and than deletes and types "Hannes" again (exceeding 300 ms) the search should not execute twice.
         */

        throw NotImplementedError()
    }
}
