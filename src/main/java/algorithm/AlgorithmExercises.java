package algorithm;

import io.reactivex.Observable;
import io.reactivex.Single;

public class AlgorithmExercises {

    /**
     * If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3, 5, 6 and 9.
     * The sum of these multiples is 23. Find the sum of all the multiples of 3 or 5 below 1000.
     */

    public Single<Integer> sumOfAllMultiples() {
        return Observable.range(1,999).filter(it -> !((it % 3 != 0) && (it % 5 != 0))).reduce(0, (a, b) -> a + b);

//        Observable<Integer> threes = range(1, 999).map(it -> it * 3).takeWhile(it -> it < 1000);
//        Observable<Integer> fives = range(1, 999).map(it -> it * 5).takeWhile(it -> it < 1000);
//
//        Observable<Integer> threesAndFives = merge(threes, fives).distinct();
//        return threesAndFives.reduce(0, (a, b) -> a + b);
    }

    /**
     * The rule that makes the Fibonacci Sequence is the next number is the sum of the previous two.
     * 0 1 1 2 3 5 8 13 21 34
     *
     * @param n the first n numbers
     * @return
     */

    public Observable<Integer> fibonacci(int n) {
        return null;
    }
}
