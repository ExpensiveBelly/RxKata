package hannesdorfmann

import io.reactivex.Observable

interface View {

    fun buttonClicked(): Observable<Boolean>
}
