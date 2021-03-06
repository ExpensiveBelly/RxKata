package operators;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MapFlatMapExercisesTest {

    private MapFlatMapSolutions exercises;

    @Before
    public void setUp() throws Exception {
        exercises = new MapFlatMapSolutions();
    }

    @Test
    public void exerciseFlatten() throws Exception {
        exercises.exerciseFlatten().test().assertResult(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void exerciseEmail() throws Exception {
        exercises.exerciseEmail().test().assertResult("myemail@gmail.com");
    }

    @Test
    public void loadRecordExercise() throws Exception {
        exercises.loadRecordsExercise().test()
                .awaitDone(5, TimeUnit.SECONDS)
                .assertResult("Sun-0", "Sun-1", "Sun-2", "Sun-3", "Sun-4", "Mon-0", "Mon-1", "Mon-2", "Mon-3", "Mon-4");
    }


    @Test
    public void speakExercise() throws Exception {
        Observable<String> alice = exercises.speak(
                "A, B C", 110);
        Observable<String> bob = exercises.speak(
                "A B, C", 90);
        Observable<String> jane = exercises.speak(
                "A B C", 100);

        TestObserver<String> test = Observable
                .concat(
                        alice.map(w -> "Alice: " + w),
                        bob.map(w -> "Bob:   " + w),
                        jane.map(w -> "Jane:  " + w)
                ).test();
        test.awaitTerminalEvent();
        test.assertResult(
                "Alice: A",
                "Alice: B",
                "Alice: C",
                "Bob:   A",
                "Bob:   B",
                "Bob:   C",
                "Jane:  A",
                "Jane:  B",
                "Jane:  C"
        ).awaitTerminalEvent();
    }
}