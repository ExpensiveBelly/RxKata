package hannesdorfmann

import io.reactivex.Observable

internal interface View {

    /**
     * Emittes the id of the person
     */
    fun onPersonClicked(): Observable<Int>

}
