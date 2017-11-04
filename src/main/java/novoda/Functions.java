package novoda;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import java.util.Arrays;
import java.util.List;

class Functions {

	private static final List<Integer> INTEGERS = Arrays.asList(0, 1, 2, 3, 4, 5, 6);

	public static Function<Throwable, Observable<Integer>> doubleEverything() {
		return throwable -> Observable.fromIterable(INTEGERS).map(multiplyBy2());
	}

	public static Function<Integer, Observable<Integer>> failIfNotEven() {
		return integer -> {
			if (isEven(integer)) {
				return Observable.just(integer);
			}
			return Observable.error(new IllegalArgumentException());
		};
	}

	public static Function<Integer, Observable<Integer>> threeTimesIfEven() {
		return integer -> {
			if (isEven(integer)) {
				return repeat(integer, 3);
			}
			return Observable.just(integer);
		};
	}

	public static Function<Integer, Integer> multiplyBy2() {
		return integer -> integer * 2;
	}

	public static Predicate<Integer> isEven() {
		return Functions::isEven;

	}

	public static Function<Integer, String> format() {
		return integer -> "Integer : " + integer;
	}

	public static Function<Integer, Observable<Integer>> threeTimes() {
		return integer -> repeat(integer, 3);
	}

	private static <T> Observable<T> repeat(T value, int count) {
		return Observable.just(value).repeat(count);
	}

	private static boolean isEven(Integer integer) {
		return (integer % 2) == 0;
	}
}
