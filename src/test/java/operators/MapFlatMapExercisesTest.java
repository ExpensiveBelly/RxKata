package operators;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapFlatMapExercisesTest {

	private MapFlatMapExercises exercises;

	@Before
	public void setUp() throws Exception {
		exercises = new MapFlatMapExercises();
	}

	@Test
	public void exerciseFlatten() throws Exception {
		exercises.exerciseFlatten().test().assertResult(1,2,3,4,5,6,7,8,9);
	}

	@Test
	public void exerciseEmail() throws Exception {
		exercises.exerciseEmail().test().assertResult("myemail@gmail.com");
	}
}