package learnrxjava;

import io.reactivex.observers.TestObserver;
import learnrxjava.types.BoxArt;
import learnrxjava.types.Movie;
import learnrxjava.types.Movies;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.reactivex.Observable.error;
import static io.reactivex.Observable.just;
import static learnrxjava.ObservableExercises.json;

public class ObservableExercisesTest {

	private ObservableSolutions solutions;

	@Before
	public void setUp() throws Exception {
		solutions = new ObservableSolutions();
	}

	@Test
	public void exerciseHello() throws Exception {
		TestObserver<String> testObserver = new TestObserver<>();
		solutions.exerciseHello().subscribe(testObserver);

		testObserver.assertComplete();
		testObserver.assertValue("Hello World!");
	}

	@Test
	public void exerciseMap() throws Exception {
		TestObserver<String> testObserver = new TestObserver<>();
		solutions.exerciseMap(just("Hello")).subscribe(testObserver);

		testObserver.assertValue("Hello Ben!");
	}

	@Test
	public void exerciseFilterMap() throws Exception {
		TestObserver<String> testObserver = new TestObserver<>();
		solutions.exerciseFilterMap(just(2, 4, 6)).subscribe(testObserver);

		testObserver.assertValueAt(0, "2-Even");
		testObserver.assertValueAt(1, "4-Even");
		testObserver.assertValueAt(2, "6-Even");

		testObserver.assertResult("2-Even", "4-Even", "6-Even");
	}

	@Test
	public void exerciseConcatMap() throws Exception {
		TestObserver<Integer> testObserver = new TestObserver<>();
		solutions.exerciseConcatMap(just(new Movies("my name", Arrays.asList(new Movie(1, "title1", 2.5), new Movie(2, "title2", 8)))))
				.subscribe(testObserver);

		testObserver.assertResult(1, 2);
	}

	@Test
	public void exerciseFlatMap() throws Exception {
		TestObserver<Integer> testObserver = new TestObserver<>();
		solutions.exerciseFlatMap(just(new Movies("my name",
				Arrays.asList(new Movie(1, "title1", 2.5, new ArrayList<>(), new ArrayList<>())))))
				.subscribe(testObserver);

		testObserver.assertNoErrors();
		testObserver.assertComplete();
		testObserver.assertValue(1);
	}

	@Test
	public void exerciseReduce() throws Exception {
		solutions.exerciseReduce(just(2, 5, 1, 3, 9)).test().assertValue(9);
	}

	@Test
	public void exerciseMovie() throws Exception {
		String url = "https://url1";
		String title = "title1";
		int id = 1;
		solutions.exerciseMovie(just(new Movies("name", Arrays.asList(new Movie(id, title, 1,
				new ArrayList<>(), Arrays.asList(new BoxArt(20, 30, url)))))))
				.test().assertValue(json("id", id, "title", title, "boxart", url));
	}

	@Test
	public void exerciseZip() throws Exception {
		solutions.exerciseZip(just("one", "two", "red", "blue"), just("fish", "fish", "fish", "fish")).test().assertResult("one fish", "two fish", "red fish", "blue fish");
	}

	@Test
	public void handleError() throws Exception {
		solutions.handleError(error(new Throwable())).test().assertValue("default-value");
	}

	@Test
	public void retry() throws Exception {
	}

}