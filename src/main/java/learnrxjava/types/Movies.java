package learnrxjava.types;

import io.reactivex.Observable;

import java.util.List;


public class Movies {
    @Override
    public String toString() {
        return "MovieList{" + "name=" + name + ", videos=" + _v + '}';
    }

    public String name;
    public Observable<Movie> videos;
    private final List<Movie> _v;

    public Movies(String name, List<Movie> videos) {
        this.name = name;
        this.videos = Observable.from(videos);
        this._v = videos;
    }
}