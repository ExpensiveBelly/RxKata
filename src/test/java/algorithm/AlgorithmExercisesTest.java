package algorithm;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AlgorithmExercisesTest {

	private AlgorithmExercises exercises;

	@Before
	public void setUp() throws Exception {
		exercises = new AlgorithmExercises();
	}

	@Test
	public void fibonacciTest() throws Exception {
		exercises.fibonacci(10).test().assertResult(0, 1, 1, 2, 3, 5, 8, 13, 21, 34);
	}
}