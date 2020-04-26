package rx3;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.exceptions.MissingBackpressureException;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class WhatIsNewInRxJava3 {

	@Test
	public void console_prints_undeliverable_exception() {
		RxJavaPlugins.setErrorHandler(System.out::println);

		PublishProcessor<Integer> main = PublishProcessor.create();
		PublishProcessor<Integer> inner = PublishProcessor.create();

		// switchMapDelayError will delay all errors
		TestSubscriber<Integer> ts = main.switchMapDelayError(v -> inner).test();

		main.onNext(1);

		// the inner fails
		inner.onError(new IOException());

		// the consumer is still clueless
		ts.assertEmpty();

		// the consumer cancels
		ts.cancel();

		// console prints
		// io.reactivex.rxjava3.exceptions.UndeliverableException:
		// The exception could not be delivered to the consumer because
		// it has already canceled/disposed the flow or the exception has
		// nowhere to  go to begin with. Further reading:
		// https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
		// | java.io.IOException
	}

	@Test
	public void call_reset_after_connectable_is_done_to_receive_items() throws InterruptedException {
		ConnectableFlowable<Integer> connectable = Flowable.range(1, 10).publish();

		// prepare consumers, nothing is signaled yet
		connectable.subscribe(/* ... */);
		connectable.subscribe(/* ... */);

		// connect, current consumers will receive items
		connectable.connect();

		// let it terminate
		Thread.sleep(500);

		// late consumers now will receive a terminal event
		connectable.subscribe(System.out::println,
				error -> {
				},
				() -> System.out.println("Late consumer Done!"));

		// reset the connectable to appear fresh again
		connectable.reset();

		// fresh consumers, they will also be ready to receive
		connectable.subscribe(
				System.out::println,
				error -> {
				},
				() -> System.out.println("2. Fresh consumer Done!")
		);

		// connect, the fresh consumer now gets the new items
		connectable.connect();
	}

	@Test
	public void publish_pause() {
		ConnectableFlowable<Integer> connectable = Flowable.range(1, 200).publish();

		connectable.connect();

		// the first consumer takes only 50 items and cancels
		connectable.take(50).test().assertValueCount(50);

		// with 3.x, the remaining items will be still available
		connectable.test().assertValueCount(150);
	}

	@Test
	public void offer_example() {
		PublishProcessor<Integer> pp = PublishProcessor.create();

		TestSubscriber<Integer> ts = pp.test();

		try {
			pp.offer(null);
		} catch (NullPointerException expected) {
		}

		// no error received
		ts.assertEmpty().assertNoErrors();

		pp.offer(1);

		// consumers are still there to receive proper items
		ts.assertValue(1);
	}

	@Test
	public void group_by_backpressure() {
		Flowable.range(1, 1000)
				.groupBy(v -> v)
				.flatMap(v -> v, 16)
				.test()
				.assertError(MissingBackpressureException.class);
	}

	@Test
	public void from_callback_upfront_cancellation_will_not_execute_callback() {
		Runnable run = mock(Runnable.class);

		Completable.fromRunnable(run)
				.test(true); // cancel upfront

		verify(run, never()).run();
	}
}
