package undeliverable;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.junit.Test;

import java.io.IOException;

public class UndeliverableErrorRx3Test {

	@Test
	public void console_prints_UudeliverableException() {
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
}
