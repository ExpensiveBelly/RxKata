package hotelbooking;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * https://github.com/teivah/algorithmic/blob/master/src/test/java/io/teivah/arrays/hotelbookings/Tests.java
 */

public class HotelBookingTest {

	@Test
	public void test1() {
		assertFalse(new HotelBooking().hotel(
				toArrayList(1, 3, 5),
				toArrayList(2, 6, 8),
				1
		));
	}

	private static ArrayList<Integer> toArrayList(Integer... n) {
		return new ArrayList<>(Arrays.asList(n));
	}

	@Test
	public void test2() {
		assertTrue(new HotelBooking().hotel(
				toArrayList(1, 1, 1),
				toArrayList(2, 3, 2),
				3
		));
	}

	@Test
	public void test3() {
		assertFalse(new HotelBooking().hotel(
				toArrayList(1, 3, 5),
				toArrayList(2, 6, 8),
				1
		));
	}

	@Test
	public void test4() {
		assertTrue(new HotelBooking().hotel(
				toArrayList(1, 1, 1),
				toArrayList(2, 3, 2),
				3
		));
	}
}
