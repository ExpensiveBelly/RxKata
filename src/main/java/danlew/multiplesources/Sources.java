package danlew.multiplesources;


import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;

/**
 * Simulates three different sources - one from memory, one from disk,
 * and one from network. In reality, they're all in-memory, but let's
 * play pretend.
 * <p>
 * Observable.create() is used so that we always return the latest data
 * to the subscriber; if you use just() it will only return the data from
 * a certain point in time.
 */
public class Sources {

	// Memory cache of data
	private Data memory = null;

	// What's currently "written" on disk
	private Data disk = null;

	// Each "network" response is different
	private int requestNumber = 0;

	// In order to simulate memory being cleared, but data still on disk
	public void clearMemory() {
		System.out.println("Wiping memory...");
		memory = null;
	}

	public Observable<Data> memory() {
		Observable<Data> observable = Observable.create(subscriber -> {
			subscriber.onNext(memory);
			subscriber.onComplete();
		});

		return observable.compose(logSource("MEMORY"));
	}

	public Observable<Data> disk() {
		Observable<Data> observable = Observable.create(subscriber -> {
			subscriber.onNext(disk);
			subscriber.onComplete();
		});

		// Cache disk responses in memory
		return observable.doOnNext(data -> memory = data)
				.compose(logSource("DISK"));
	}

	public Observable<Data> network() {
		Observable<Data> observable = Observable.create(subscriber -> {
			requestNumber++;
			subscriber.onNext(new Data("Server Response #" + requestNumber));
			subscriber.onComplete();
		});

		// Save network responses to disk and cache in memory
		return observable.doOnNext(data -> {
			disk = data;
			memory = data;
		})
				.compose(logSource("NETWORK"));
	}

	// Simple logging to let us know what each source is returning
	ObservableTransformer<Data, Data> logSource(final String source) {
		return dataObservable -> dataObservable.doOnNext(data -> {
			if (data == null) {
				System.out.println(source + " does not have any data.");
			} else if (!data.isUpToDate()) {
				System.out.println(source + " has stale data.");
			} else {
				System.out.println(source + " has the data you are looking for!");
			}
		});
	}

}
