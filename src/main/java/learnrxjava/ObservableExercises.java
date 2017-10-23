package learnrxjava;


import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import learnrxjava.types.BoxArt;
import learnrxjava.types.JSON;
import learnrxjava.types.Movie;
import learnrxjava.types.Movies;

public class ObservableExercises {

    // This function can be used to build JSON objects within an expression
    public static JSON json(Object... keyOrValue) {
        JSON json = new JSON();

        for (int counter = 0; counter < keyOrValue.length; counter += 2) {
            json.put((String) keyOrValue[counter], keyOrValue[counter + 1]);
        }

        return json;
    }

    /**
     * Return an Observable that emits a single value "Hello World!"
     *
     * @return "Hello World!"
     */
    public Observable<String> exerciseHello() {
        return Single.just("Hello World!").toObservable();
    }

    /**
     * Transform the incoming Observable from "Hello" to "Hello [Name]" where [Name] is your name.
     *
     * @param "Hello Name!"
     */
    public Observable<String> exerciseMap(Observable<String> hello) {
        return hello.map(greeting -> greeting + " Daniel!");
    }

    /**
     * Given a stream of numbers, choose the even ones and return a stream like:
     * <p>
     * 2-Even
     * 4-Even
     * 6-Even
     */
    public Observable<String> exerciseFilterMap(Observable<Integer> nums) {
        return nums.filter(num -> (num % 2) == 0).map(num -> num + "-Even");
    }

    /**
     * Flatten out all video in the stream of Movies into a stream of videoIDs
     *
     * @param movies
     * @return Observable of Integers of Movies.videos.id
     */
    public Observable<Integer> exerciseConcatMap(Observable<Movies> movies) {
        return movies.concatMap(moviesVolume -> moviesVolume.videos.map(movie -> movie.id));
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
        return movies.flatMap(moviesVolume -> moviesVolume.videos.map(movie -> movie.id));
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
        return movies.flatMap(movielist -> movielist.videos)
                .flatMap(movie -> movie.boxarts
                        .reduce(this::maxBoxArt)
                        .flatMapObservable(movieJsonObservable(movie)));
    }

    private Function<BoxArt, ObservableSource<? extends JSON>> movieJsonObservable(Movie movie) {
        return boxArt -> Observable.just(movieJson(movie, boxArt));
    }

    private JSON movieJson(Movie movie, BoxArt boxArt) {
        return json("id", movie.id, "title", movie.title, "smallestBoxArt", boxArt.url);
    }

    private BoxArt maxBoxArt(BoxArt boxArt, BoxArt boxArt2) {
        return boxArtSize(boxArt) <= boxArtSize(boxArt2) ? boxArt : boxArt2;
    }

    private int boxArtSize(BoxArt boxArt) {
        return boxArt.height * boxArt.width;
    }

    /**
     * Combine 2 streams into pairs using zip.
     * <p>
     * a -> "one", "two", "red", "blue"
     * b -> "fish", "fish", "fish", "fish"
     * output -> "one fish", "two fish", "red fish", "blue fish"
     */
    public Observable<String> exerciseZip(Observable<String> verbs, Observable<String> nouns) {
        return Observable.zip(verbs, nouns, (verb, noun) -> verb + " " + noun);
    }

    /**
     * Don't modify any values in the stream but do handle the error
     * and replace it with "default-value".
     */
    public Observable<String> handleError(Observable<String> data) {
        return data.onErrorReturn(e -> "default-value");
    }

    /**
     * The data stream fails intermittently so return the stream
     * with retry capability.
     */
    public Observable<String> retry(Observable<String> data) {
        return Observable.error(new RuntimeException("Not Implemented"));
    }
}
