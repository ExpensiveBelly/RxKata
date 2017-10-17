package algorithm;

import org.junit.Before;
import org.junit.Test;

public class AlgorithmExercisesTest {

	private AlgorithmExercises exercises;

	@Before
	public void setUp() throws Exception {
		exercises = new AlgorithmExercises();
	}

	@Test
	public void sumOfAllMultiplesTest() throws Exception {
		exercises.sumOfAllMultiples().test().assertResult(233168);
	}

	@Test
	public void fibonacciTest() throws Exception {
		exercises.fibonacci(10).test().assertResult(0, 1, 1, 2, 3, 5, 8, 13, 21, 34);
	}
}