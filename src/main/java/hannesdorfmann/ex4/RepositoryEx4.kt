package hannesdorfmann.ex4

import hannesdorfmann.types.Person
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class RepositoryEx4(private val viewEx4: ViewEx4, private val backendEx4: BackendEx4) {


    /**
     * Implement the search call according the following criteria:
     * - The search query string must contain at least 3 characters
     * - To save backend traffic: only search if search query hasn't changed within the last 300 ms
     * - If the user is typing fast "Hannes" and than deletes and types "Hannes" again (exceeding 300 ms) the search should not execute twice.
     */
    fun search(): Observable<List<Person>> =
            viewEx4.onSearchTextchanged()
                    .debounce(300, TimeUnit.MILLISECONDS)
                    .distinctUntilChanged()
                    .filter{it.length >= 3}
                    .switchMap { backendEx4.searchfor(it)
                            .filter { !it.isEmpty() }  //unsure why we need this...
                    }
}
