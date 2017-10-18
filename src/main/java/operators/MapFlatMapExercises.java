package operators;

import com.pacoworks.rxsealedunions.Union3;
import com.pacoworks.rxsealedunions.generic.GenericUnions;
import io.reactivex.Observable;

import java.time.DayOfWeek;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * As a rule of thumb, you use flatMap() for the following situations:
 * <p>
 * The result of transformation in map() must be an Observable.
 * For example, performing long-running, asynchronous operation on each element of the stream without blocking.
 * <p>
 * You need a one-to-many transformation, a single event is expanded into multiple sub-events.
 * For example, a stream of customers is translated into streams of their orders, for which each customer
 * can have an arbitrary number of orders.
 */

public class MapFlatMapExercises {

	/**
	 * Given a stream t[ [1,2,3],[4,5,6],[7,8,9] ] which has "two levels", flatten it in a "one level" structure :
	 * [ 1,2,3,4,5,6,7,8,9 ]
	 */
	public Observable<Integer> exerciseFlatten() {
		Observable<Union3.Factory> pairObservable = Observable
				.just(union3(1, 2, 3),
						union3(4, 5, 6),
						union3(7, 8, 9));

		return null;
	}

	private Union3.Factory<Integer, Integer, Integer> union3(int first, int second, int third) {
		Union3.Factory<Integer, Integer, Integer> union3 = GenericUnions.tripletFactory();
		union3.first(first);
		union3.second(second);
		union3.third(third);
		return union3;
	}

	/**
	 * You have a booking that has an optional user. And the optional user has an optional email.
	 * Write a method getEmail() that gives you the email option of a booking
	 *
	 * @return
	 */

	public Observable<String> exerciseEmail() {
		Booking booking = new Booking(new Booking.User("myemail@gmail.com"));

		return null;
	}

	static class Booking {
		private final Optional<User> user;

		public Booking(User user) {
			this.user = Optional.ofNullable(user);
		}

		Optional<User> getUser() {
			return user;
		}

		private static class User {
			Optional<String> getEmail() {
				return email;
			}

			private final Optional<String> email;

			public User(String email) {
				this.email = Optional.ofNullable(email);
			}
		}
	}

	/**
	 * Return an Observable that shows the records as specified in the test using `loadRecordsFor`
	 */

	public Observable<String> loadRecordsExercise() {
		/*return*/ Observable
				.just(DayOfWeek.SUNDAY, DayOfWeek.MONDAY);
				/* INSERT CODE HERE */

		return null;
	}


	Observable<String> loadRecordsFor(DayOfWeek dow) {
		switch (dow) {
			case SUNDAY:
				return Observable
						.interval(90, MILLISECONDS)
						.take(5)
						.map(i -> "Sun-" + i);
			case MONDAY:
				return Observable
						.interval(65, MILLISECONDS)
						.take(5)
						.map(i -> "Mon-" + i);
		}
		return null;
	}
}
