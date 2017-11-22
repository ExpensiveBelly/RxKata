package hannesdorfmann

import io.reactivex.Observable

class Repository(private val view: View, private val backend: Backend) {

    fun loadPersons(): Observable<List<Person>> {

        //
        // TODO: Load the list of persons on each button click from the view. So basically
        //
        // return view.buttonClicks().<-- your code to somehow load data from backend-->
        //
        //

        throw RuntimeException("Not implemented")
    }
}
