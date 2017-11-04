package learnrxjava;

import io.reactivex.Observable;
import learnrxjava.types.BoxArt;
import learnrxjava.types.Movie;
import learnrxjava.types.Movies;
import org.junit.Before;
import org.junit.Test;

import static io.reactivex.Observable.error;
import static io.reactivex.Observable.just;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static learnrxjava.ObservableExercises.json;

public class ObservableExercisesTest {

    private ObservableSolutions exercises;

    private static final Movie MEMENTO = new Movie(1, "Memento", 5, emptyList(),
            asList(new BoxArt(1, 20, "mementoSmall.jpg"), new BoxArt(1, 30, "mementoBig.jpg")));
    private static final Movie INCEPTION = new Movie(3, "Inception", 5, emptyList(),
            asList(new BoxArt(2, 20, "inceptionSmall.jpg"), new BoxArt(2, 30, "mementoBig.jpg")));
    private static final Movie HALF_BLOOD_PRINCE = new Movie(5, "Harry Potter and the Half-Blood Prince", 5, emptyList(),
            asList(new BoxArt(4, 20, "halfBloodSmall.jpg"), new BoxArt(4, 30, "mementoBig.jpg")));
    private static final Movie DEATHLY_HALLOWS_PART_1 = new Movie(7, "Harry Potter and the Deathly Hallows: Part 1", 5, emptyList(),
            asList(new BoxArt(5, 20, "hallowsSmall.jpg"), new BoxArt(5, 30, "hallowsBig.jpg")));

    private static final Movies NOLAN_MOVIES = new Movies("Christopher Nolan Movies", asList(MEMENTO, INCEPTION));
    private static final Movies HARRY_POTTERS_MOVIES = new Movies("Harry Potter Movies", asList(HALF_BLOOD_PRINCE, DEATHLY_HALLOWS_PART_1));

    @Before
    public void setUp() throws Exception {
        exercises = new ObservableSolutions();
    }

    @Test
    public void exerciseHello() throws Exception {
        exercises.exerciseHello().test().assertResult("Hello World!");
    }

    @Test
    public void exerciseMap() throws Exception {
        exercises.exerciseMap(just("Hello"))
                .test().assertValue("Hello Ben!");
    }

    @Test
    public void exerciseFilterMap() throws Exception {
        exercises.exerciseFilterMap(Observable.fromArray(1, 2, 3, 4, 5, 6))
                .test().assertValues("2-Even", "4-Even", "6-Even");
    }

    @Test
    public void exerciseConcatMap() throws Exception {
        exercises.exerciseConcatMap(just(NOLAN_MOVIES, HARRY_POTTERS_MOVIES))
                .test().assertValues(1, 3, 5, 7);
    }

    @Test
    public void exerciseFlatMap() throws Exception {
        exercises.exerciseFlatMap(just(NOLAN_MOVIES, HARRY_POTTERS_MOVIES))
                .test().assertValues(1, 3, 5, 7);
    }

    @Test
    public void exerciseReduce() throws Exception {
        exercises.exerciseReduce(just(1, 2, 200, 4, 5)).test().assertValue(200);
    }

    @Test
    public void exerciseMovie() throws Exception {
        exercises.exerciseMovie(just(NOLAN_MOVIES, HARRY_POTTERS_MOVIES))
                .test().assertResult(
                json("id", 1, "title", "Memento", "smallestBoxArt", "mementoSmall.jpg"),
                json("id", 3, "title", "Inception", "smallestBoxArt", "inceptionSmall.jpg"),
                json("id", 5, "title", "Harry Potter and the Half-Blood Prince", "smallestBoxArt", "halfBloodSmall.jpg"),
                json("id", 7, "title", "Harry Potter and the Deathly Hallows: Part 1", "smallestBoxArt", "hallowsSmall.jpg")
        );
    }

    @Test
    public void exerciseZip() throws Exception {
        exercises.exerciseZip(just("one", "two", "red", "blue"),
                just("fish", "fish", "fish", "fish"))
                .test()
                .assertResult("one fish", "two fish", "red fish", "blue fish");
    }

    @Test
    public void handleError() throws Exception {
        exercises.handleError(error(new Throwable()))
                .test().assertValue("default-value");
    }

    @Test
    public void retry() throws Exception {
    }
}