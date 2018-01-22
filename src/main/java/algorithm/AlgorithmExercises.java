package algorithm;

import java.util.Iterator;

import io.reactivex.Observable;
import io.reactivex.Single;

public class AlgorithmExercises {

    /**
     * If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3, 5, 6 and 9.
     * The sum of these multiples is 23. Find the sum of all the multiples of 3 or 5 below 1000.
     */

    public Single<Integer> sumOfAllMultiples() {
        return Observable.range(1, 999)
                .filter(num -> num % 3 == 0 | num % 5 == 0)
                .reduce(0, (acc, num) -> acc + num);
    }

    /**
     * The rule that makes the Fibonacci Sequence is the next number is the sum of the previous two.
     * 0 1 1 2 3 5 8 13 21 34
     *
     * @param n the first n numbers
     * @return
     */

    public Observable<Integer> fibonacci(int n) {
        return Observable.fromIterable(fibonacciIterable())
                .take(n);
    }

    private Iterable<Integer> fibonacciIterable() {
        return () -> new Iterator<Integer>() {
            int first = -1;
            int second = -1;
            @Override
            public boolean hasNext() {
                return true; //infinite
            }

            @Override
            public Integer next() {
                if (first == -1) {
                    first = 0;
                    return 0;
                } else if (second == -1) {
                    second = 1;
                    return 1;
                } else {
                    int fib = first + second;
                    first = second;
                    second = fib;
                    return fib;
                }
            }
        };
    }

    /**
     * Factorial of n is the product of all positive descending integers
     * <p>
     * 4! = 4 * 3 * 2 * 1 = 24
     * 5! = 5 * 4 * 3 * 2 * 1 = 120
     */

    Observable<Integer> factorial(int n) {
        return Observable.range(2, n-1)
                .scan(1, (acc, num) -> acc * num);
    }
}
