package novoda

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import java.util.*


class BasicSolutions {

    private val INTEGERS = Arrays.asList(0, 1, 2, 3, 4, 5, 6)

    /**
    - Multiply all INTEGERS elements by 2
    - Repeat 3 times every even element and emit once every odd element
    - Send elements until you encounter an odd element then fail, restart and multiply by 2 the entire sequence
    - Prepend the string "Integer : " in front of every element
     */

    fun basicExercise(): Observable<String> {
        return Observable.fromIterable(INTEGERS)
                .map<Int>(Functions.multiplyBy2())
                .flatMap(Functions.threeTimesIfEven())
                .flatMap(Functions.failIfNotEven())
                .onErrorResumeNext(Functions.doubleEverything())
                .map<String>(format())
    }

    //FIXME: Kotlin functions produce a different result at the moment

    private fun doubleEverything(): Function<Throwable, Observable<Int>> {
        return Function { Observable.fromIterable(INTEGERS).map(multiplyBy2()) }
    }

    private fun failIfNotEven(): Function<Int, Observable<Int>> {
        return Function { integer ->
            if (isEven(integer)) {
                Observable.just<Int>(integer)
            }
            Observable.error<Int>(IllegalArgumentException())
        }
    }

    private fun threeTimesIfEven(): Function<Int, Observable<Int>> {
        return Function { integer ->
            if (isEven(integer)) {
                repeat(integer, 3)
            }
            Observable.just<Int>(integer)
        }
    }

    private fun multiplyBy2(): Function<Int, Int> {
        return Function { integer -> integer * 2 }
    }

    private fun format(): Function<Int, String> {
        return Function { integer -> "Integer : " + integer }
    }

    private fun <T> repeat(value: T, count: Int): Observable<T> {
        return Observable.just(value).repeat(count.toLong())
    }

    private fun isEven(integer: Int): Boolean {
        return integer % 2 == 0
    }

    private val SENTENCES = Arrays.asList("This is the first sentence", "I want those to be enumerated", "How would you ask?", "That is yours to find out!")

    private val INFINITE_ITERABLE = object : Iterable<Int> {
        override fun iterator(): Iterator<Int> {
            return IntegerOperator()
        }
    }

    /**
    - Get the 20 first integers from INFINITE_ITERABLE
    - Enumerate the SENTENCES by adding their index in front of it.
    - Concatenate the sequences into one line.
     */

    fun infiniteExercise(): Observable<String> {
        return Observable.fromIterable(INFINITE_ITERABLE)
                .take(20)
                .zipWith(Observable.fromIterable(SENTENCES), BiFunction { t1: Int, t2: String -> "" + t1 + ":" + t2 }, false)
                .reduce { t1: String, t2: String -> t1 + " " + t2 }
                .toObservable()
    }
}