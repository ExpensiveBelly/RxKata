package algorithm;

import org.junit.Before;
import org.junit.Test;

public class AlgorithmExercisesTest {

	private AlgorithmSolutions exercises;

	@Before
	public void setUp() throws Exception {
		exercises = new AlgorithmSolutions();
	}

	@Test
	public void sumOfAllMultiplesTest() throws Exception {
		exercises.sumOfAllMultiples().test().assertResult(233168);
	}

	@Test
	public void fibonacciTest() throws Exception {
		exercises.fibonacci(10).test().assertResult(0, 1, 1, 2, 3, 5, 8, 13, 21, 34);
	}

	@Test
	public void factorialTest() throws Exception {
		exercises.factorial(4).test().assertResult(1, 2, 6, 24);
		exercises.factorial(5).test().assertResult(1, 2, 6, 24, 120);
		exercises.factorial(10).test().assertResult(1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800);
	}
}