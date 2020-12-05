package playground

import io.reactivex.subjects.PublishSubject
import org.junit.Test

/*
https://rxmarbles.com/#withLatestFrom
 */

class WithLatestFromDoesNotWaitTest {

	@Test
	fun `once the emission happens it does not wait for the other sources, if they are not available then it does not call the combiner`() {
		val source1 = PublishSubject.create<Int>()
		val source2 = PublishSubject.create<Int>()
		val source3 = PublishSubject.create<Int>()

		val testObserver = source1.withLatestFrom(source2, source3, { t1, t2, t3 ->
			t1 + t2 + t3
		}).test()

		val SOURCE1 = 1
		val SOURCE2 = 2
		val SOURCE3 = 3
		val SOURCE4 = 4

		source1.onNext(SOURCE1)
		testObserver.assertNoValues()
		source2.onNext(SOURCE2)
		testObserver.assertNoValues()
		source3.onNext(SOURCE3)
		testObserver.assertNoValues()

		source1.onNext(SOURCE4)
		testObserver.assertValue(SOURCE4 + SOURCE2 + SOURCE3)
	}
}