package novoda

import io.reactivex.Observable
import java.util.*

class BasicExercises {

    private val INTEGERS = Arrays.asList(0, 1, 2, 3, 4, 5, 6)

    /**
    - Multiply all INTEGERS elements by 2
    - Repeat 3 times every even element and emit once every odd element
    - Send elements until you encounter an odd element then fail, restart and multiply by 2 the entire sequence
    - Prepend the string "Integer : " in front of every element
     */

    fun basicExercise(): Observable<String> {
        return Observable.error<String>(RuntimeException("Not Implemented"))
    }
}