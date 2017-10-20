package operators;

import io.reactivex.Observable;
import org.javatuples.Pair;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Speak {

    public static Observable<String> speak(String quote, long millisPerChar) {
        String[] tokens = quote.replaceAll("[:,]", "").split(" ");
        Observable<String> words = Observable.fromArray(tokens);
        Observable<Long> absoluteDelay = words
                .map(String::length)
                .map(len -> len * millisPerChar)
                .scan((total, current) -> total + current);
        return words
                .zipWith(absoluteDelay.startWith(0L), (s, l) -> new Pair<>(s, l))
                .flatMap(pair -> Observable.just(pair.getValue0())
                        .delay(pair.getValue1(), MILLISECONDS));
    }
}
