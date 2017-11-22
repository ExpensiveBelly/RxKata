package hannesdorfmann

import io.reactivex.Observable

interface Backend {
    fun loadPersons(): Observable<List<Person>>
}
