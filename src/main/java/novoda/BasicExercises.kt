package novoda

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.TestScheduler
import novoda.types.IntegerOperator
import java.util.*
import java.util.concurrent.TimeUnit


class BasicExercises {

    private val INTEGERS = Arrays.asList(0, 1, 2, 3, 4, 5, 6)

    /**
    - Repeat 3 times every even element and emit once every odd element using INTEGERS as a source of data
    - Send elements until you encounter an odd element then fail, restart and multiply by 2 the entire sequence
    - Prepend the string "Integer : " in front of every element
     */

    fun basicExercise(): Observable<String> =
            Observable.fromIterable(INTEGERS)
                    .let { obvs ->
                        obvs.concatMap {
                            if (it % 2 == 0)
                                Observable.just(it)
                                        .repeat(3)
                            else
                                Observable.error(IllegalArgumentException("That's odd..."))
                        }
                                .onErrorResumeNext(obvs.map { it * 2 })
                                .map { "Integer : $it" }
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

    fun infiniteExercise(): Observable<String> =
            Observable.zip(
                    Observable.fromIterable(INFINITE_ITERABLE)
                            .take(20),
                    Observable.fromIterable(SENTENCES),
                    BiFunction<Int, String, String> { num, sentence -> "$num:$sentence" })
                    .collectInto(StringBuilder(), { buffer, string -> buffer.append(' ').append(string) })
                    .map { it.deleteCharAt(0) }
                    .map { it.toString() }
                    .toObservable()


    /**
     * Implement a timer that emits an item every second. Numbers to be emitted: 1 to 6
     */

    fun timer(scheduler: TestScheduler): Observable<Long> =
            Observable.interval(1, TimeUnit.SECONDS, scheduler)
                    .take(6)
                    .map { it + 1 }

    /**
     * Implement count() operator (counts the amount of items in an observable)
     * Hint: You can use reduce() operator
     */

    fun count(observable: Observable<Char>): Single<Int> =
            observable
                    .reduce(0, { acc, char -> acc + 1 })
}