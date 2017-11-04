package operators;

import io.reactivex.Observable;
import org.javatuples.Triplet;

import java.time.DayOfWeek;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class MapFlatMapSolutions {

	/**
	 * Given a stream t[ [1,2,3],[4,5,6],[7,8,9] ] which has "two levels", flatten it in a "one level" structure :
	 * [ 1,2,3,4,5,6,7,8,9 ]
	 */
	public Observable<Integer> exerciseFlatten() {
		Observable<Triplet<Integer, Integer, Integer>> pairObservable = Observable
				.just(new Triplet<>(1, 2, 3),
						new Triplet<>(4, 5, 6),
						new Triplet<>(7, 8, 9));

		return null;
	}

	/**
	 * You have a booking that has an optional user. And the optional user has an optional email.
	 * Write a method getEmail() that gives you the email option of a booking
	 *
	 * @return
	 */

	public Observable<String> exerciseEmail() {
		MapFlatMapSolutions.Booking booking = new MapFlatMapSolutions.Booking(new MapFlatMapSolutions.Booking.User("myemail@gmail.com"));

		return null;
	}

	static class Booking {
		private final Optional<MapFlatMapSolutions.Booking.User> user;

		public Booking(MapFlatMapSolutions.Booking.User user) {
			this.user = Optional.ofNullable(user);
		}

		Optional<MapFlatMapSolutions.Booking.User> getUser() {
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
	 * Return an Observable that shows the records as specified in the test using `loadRecordsFor` method as the source of
	 * the data
	 */

	public Observable<String> loadRecordsExercise() {
		/*return*/ Observable
				.just(DayOfWeek.SUNDAY, DayOfWeek.MONDAY);
				/* loadRecordsFor() */

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

	/**
	 * Take an arbitrary text in String and split it to words, removing punctuation using a
	 * regular expression. Now, for each word we calculate how much it takes to say that word,
	 * simply by multiplying the word length by millisPerChar (Check 'Speak' class)
	 * Then, we would like to spread words over time, so that each word appears in the resulting stream after the delay calculated
	 * @param quote
	 * @param millisPerChar
	 * @see Speak
	 * @return
	 */

	public Observable<String> speak(String quote, long millisPerChar) {
		/*Speak.speak(quote, millisPerChar);*/
		return null;
	}
}
