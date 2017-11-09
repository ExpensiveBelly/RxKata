package algorithm;

import io.reactivex.Observable;
import io.reactivex.Single;

import java.math.BigInteger;

public class AlgorithmSolutions {

	/**
	 * If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3, 5, 6 and 9.
	 * The sum of these multiples is 23. Find the sum of all the multiples of 3 or 5 below 1000.
	 */

	public Single<Integer> sumOfAllMultiples() {
		return Observable.range(1, 999).filter(it -> !((it % 3 != 0) && (it % 5 != 0))).reduce(0, (a, b) -> a + b);
	}

	/**
	 * The rule that makes the Fibonacci Sequence is the next number is the sum of the previous two.
	 * 0 1 1 2 3 5 8 13 21 34
	 *
	 * @param n the first n numbers
	 * @return
	 */

	public Observable<Integer> fibonacci(int n) {
		return Observable.range(1, n - 1)
				.map(integer -> new Fib(0, 1))
				.scan((fib, fib2) -> new Fib(fib.n2, fib.n1 + fib.n2))
				.map(fib -> fib.n2)
				.startWith(0);
	}

	static class Fib {
		final int n1;
		final int n2;

		Fib(int n1, int n2) {
			this.n1 = n1;
			this.n2 = n2;
		}
	}

	/**
	 * Factorial of n is the product of all positive descending integers
	 * <p>
	 * 4! = 4 * 3 * 2 * 1 = 24
	 * 5! = 5 * 4 * 3 * 2 * 1 = 120
	 */

	Observable<Integer> factorial(int n) {
		return Observable
				.range(2, n-1)
				.scan(BigInteger.ONE, (big, cur) ->
						big.multiply(BigInteger.valueOf(cur)))
				.map(bigInteger -> bigInteger.intValue());
	}
}
