package learnrxjava;


import io.reactivex.Maybe;
import io.reactivex.Observable;
import learnrxjava.types.JSON;
import learnrxjava.types.Movies;

import java.util.Arrays;

public class ObservableExercises {

    /**
     * Return an Observable that emits a single value "Hello World!"
     *
     * @return "Hello World!"
     */
    public Observable<String> exerciseHello() {
        return Observable.just("Hello World!");
    }

    /**
     * Transform the incoming Observable from "Hello" to "Hello [Name]" where [Name] is your name.
     *
     * @param "Hello Name!"
     */
    public Observable<String> exerciseMap(Observable<String> hello) {
        return hello.map(s -> s + " Todd!");
    }

    /**
     * Given a stream of numbers, choose the even ones and return a stream like:
     * <p>
     * 2-Even
     * 4-Even
     * 6-Even
     */
    public Observable<String> exerciseFilterMap(Observable<Integer> nums) {
        return nums.filter(integer -> integer % 2 == 0)
                .map(integer -> integer + "-Even");
    }

    /**
     * Flatten out all video in the stream of Movies into a stream of videoIDs
     *
     * @param movies
     * @return Observable of Integers of Movies.videos.id
     */
    public Observable<Integer> exerciseConcatMap(Observable<Movies> movies) {
        return movies.concatMap(movies1 -> movies1.videos.map(movie -> movie.id));
    }

    /**
     * Flatten out all video in the stream of Movies into a stream of videoIDs
     * <p>
     * Use flatMap this time instead of concatMap. In Observable streams
     * it is almost always flatMap that is wanted, not concatMap as flatMap
     * uses merge instead of concat and allows multiple concurrent streams
     * whereas concat only does one at a time.
     * <p>
     * We'll see more about this later when we add concurrency.
     *
     * @param movies
     * @return Observable of Integers of Movies.videos.id
     */
    public Observable<Integer> exerciseFlatMap(Observable<Movies> movies) {
        return movies.flatMap(movies1 -> movies1.videos.map(movie -> movie.id));
    }

    /**
     * Retrieve the largest number.
     * <p>
     * Use reduce to select the maximum value in a list of numbers.
     */
    public Maybe<Integer> exerciseReduce(Observable<Integer> nums) {
        return nums.reduce(Math::max);
    }

    /**
     * Retrieve the id, title, and <b>smallest</b> box art url for every video.
     * <p>
     * Now let's try combining reduce() with our other functions to build more complex queries.
     * <p>
     * This is a variation of the problem we solved earlier, where we retrieved the url of the boxart with a
     * width of 150px. This time we'll use reduce() instead of filter() to retrieve the _smallest_ box art in
     * the boxarts list.
     * <p>
     * See Exercise 19 of ComposableListExercises
     */
    public Observable<JSON> exerciseMovie(Observable<Movies> movies) {
        return movies.flatMap(movies1 -> movies1.videos
                .flatMap(movie ->
                        movie.boxarts.reduce((boxArt, boxArt2) ->
                                boxArt.height * boxArt.width <= boxArt2.height * boxArt2.width ? boxArt : boxArt2)
                                .flatMapObservable(boxArt -> Observable.just(json("smallestBoxArt", boxArt.url, "id", movie.id, "title", movie.title)))));
    }

    /**
     * Combine 2 streams into pairs using zip.
     * <p>
     * a -> "one", "two", "red", "blue"
     * b -> "fish", "fish", "fish", "fish"
     * output -> "one fish", "two fish", "red fish", "blue fish"
     */
    public Observable<String> exerciseZip(Observable<String> a, Observable<String> b) {
        return a.zipWith(b, (s, s2) -> s + " " + s2);
    }

    /**
     * Don't modify any values in the stream but do handle the error
     * and replace it with "default-value".
     */
    public Observable<String> handleError(Observable<String> data) {
        return data.onErrorReturn(throwable -> "default-value");
    }

    // This function can be used to build JSON objects within an expression
    public static JSON json(Object... keyOrValue) {
        JSON json = new JSON();

        for (int counter = 0; counter < keyOrValue.length; counter += 2) {
            json.put((String) keyOrValue[counter], keyOrValue[counter + 1]);
        }

        return json;
    }
}
