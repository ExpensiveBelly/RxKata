package operators;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MapFlatMapExercisesTest {

    private MapFlatMapExercises exercises;

    @Before
    public void setUp() throws Exception {
        exercises = new MapFlatMapExercises();
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

        /**
         * This Observable is incorrect and it's causing the test to fail
         */

        TestObserver<String> test = Observable
                .merge(
                        alice.map(w -> "Alice: " + w),
                        bob.map(w -> "Bob:   " + w),
                        jane.map(w -> "Jane:  " + w)
                ).test();
        test.awaitTerminalEvent();
        //bit of a crappy test - should use test scheduler & go forward in time
        //that way the test is not slow - also can verify that each word is spoken after that amount of time exactly
        test.assertResult(
                "Alice: A",
                "Bob:   A",
                "Jane:  A",
                "Bob:   B",
                "Jane:  B",
                "Alice: B",
                "Bob:   C",
                "Jane:  C",
                "Alice: C"
        ).awaitTerminalEvent();
    }
}