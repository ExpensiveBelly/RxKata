package learnrxjava.types;

import io.reactivex.Observable;

import java.util.List;

public class Movie {
    @Override
    public String toString() {
        return "Video{" + "id=" + id + ", title=" + title + ", rating=" + rating + ", bookmarks=" + _bookmarks + ", boxarts=" + _boxarts + ", interestingMoments=" + _interestingMoments + '}';
    }

    public int id;
    public String title;
    public double rating;
    public Observable<Bookmark> bookmarks;
    public Observable<BoxArt> boxarts;
    public Observable<InterestingMoment> interestingMoments;
    
    private List<Bookmark> _bookmarks;
    private List<BoxArt> _boxarts;
    private List<InterestingMoment> _interestingMoments;

    public Movie(int id, String title, double rating) {
        this.id = id;
        this.title = title;
        this.rating = rating;
    }

    public Movie(int id, String title, double rating, List<Bookmark> bookmarks, List<BoxArt> boxarts) {
        this(id, title, rating);
        this.bookmarks = Observable.fromIterable(bookmarks);
        this.boxarts = Observable.fromIterable(boxarts);
        this._bookmarks = bookmarks;
        this._boxarts = boxarts;
    }

    public Movie(int id, String title, double rating, List<Bookmark> bookmarks, List<BoxArt> boxarts, List<InterestingMoment> interestingMoments) {
        this(id, title, rating);
        this.bookmarks = Observable.fromIterable(bookmarks);
        this.boxarts = Observable.fromIterable(boxarts);
        this.interestingMoments = Observable.fromIterable(interestingMoments);
        
        this._bookmarks = bookmarks;
        this._boxarts = boxarts;
        this._interestingMoments = interestingMoments;
    }
}