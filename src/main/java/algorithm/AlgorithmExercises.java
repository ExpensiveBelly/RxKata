package algorithm;

import io.reactivex.Observable;
import io.reactivex.Single;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AlgorithmExercises {

	/**
	 * If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3, 5, 6 and 9.
	 * The sum of these multiples is 23. Find the sum of all the multiples of 3 or 5 below 1000.
	 */

	public Single<Integer> sumOfAllMultiples() {
		return Single.error(new NotImplementedException());
	}

	/**
	 * The rule that makes the Fibonacci Sequence is the next number is the sum of the previous two.
	 * 0 1 1 2 3 5 8 13 21 34
	 *
	 * @param n the first n numbers
	 * @return
	 */

	public Observable<Integer> fibonacci(int n) {
		return Observable.error(new NotImplementedException());
	}

	/**
	 * Factorial of n is the product of all positive descending integers
	 * <p>
	 * 4! = 4 * 3 * 2 * 1 = 24
	 * 5! = 5 * 4 * 3 * 2 * 1 = 120
	 */

	Observable<Integer> factorial(int n) {
		return Observable.error(new NotImplementedException());
	}
}
