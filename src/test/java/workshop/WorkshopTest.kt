package workshop

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WorkshopTest {

	@Test()
	fun producingUnitsTests() {
		Workshop().run {
			producingUnits(0).test().assertNoValues()
			producingUnits(1).test().assertValueCount(1).assertValue(Unit)
			producingUnits(2).toList().test().assertValue(listOf(Unit, Unit))
			producingUnits(3).toList().test().assertValue(listOf(Unit, Unit, Unit))
			for (i in 1..100 step 7) {
				producingUnits(i).toList().test().assertValue(List(i) { Unit })
			}
		}
	}

	@Test()
	fun toToggleTests() {
		Workshop().run {
			producingUnits(0).toToggle().test().assertNoValues()
			producingUnits(1).toToggle().toList().test().assertValueCount(1).assertValue(listOf(true))
			producingUnits(2).toToggle().toList().test().assertValue(listOf(true, false))
			producingUnits(3).toToggle().toList().test().assertValue(listOf(true, false, true))
			producingUnits(4).toToggle().toList().test().assertValue(listOf(true, false, true, false))
		}
	}

	@Test()
	fun toNextNumbersTests() {
		Workshop().run {
			producingUnits(0).toNextNumbers().toList().test().assertValue(listOf())
			producingUnits(1).toNextNumbers().toList().test().assertValue(listOf(1))
			producingUnits(2).toNextNumbers().toList().test().assertValue(listOf(1, 2))
			producingUnits(3).toNextNumbers().toList().test().assertValue(listOf(1, 2, 3))
			for (i in 1..100 step 7) {
				val list = List(i) { it + 1 }
				list.map { Unit }.toObservable().toNextNumbers().toList().test().assertValue(list)
			}
		}
	}

	@Test()
	fun withHistoryTests() {
		Workshop().run {
			producingUnits(0).withHistory().toList().test().assertValue(listOf(listOf()))
			producingUnits(1).withHistory().toList().test().assertValue(listOf(listOf(), listOf(Unit)))
			producingUnits(2).withHistory().toList().test()
				.assertValue(listOf(listOf(), listOf(Unit), listOf(Unit, Unit)))

			producingUnits(2).toNextNumbers().withHistory().toList().test()
				.assertValue(listOf(listOf(), listOf(1), listOf(1, 2)))
			producingUnits(2).toToggle().withHistory().toList().test()
				.assertValue((listOf(listOf(), listOf(true), listOf(true, false))))

			val testScheduler = TestScheduler()

			Observable.create<Any> { emitter ->
				emitter.onNext("A")
				val executor = Executors.newSingleThreadExecutor()
				Schedulers.from(executor).scheduleDirect({
					emitter.onNext(10)
					emitter.onNext("C")
					emitter.onComplete()
				}, 100, TimeUnit.MILLISECONDS)
			}

			val observable: Observable<Any> =
				Observable.just<Any>("A")
					.delay(100, TimeUnit.MILLISECONDS, testScheduler)
					.concatWith(Observable.just<Any>(10, "C"))

			val testObserver = observable.withHistory().test()
			testObserver.assertValue(listOf())
			testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS)
			testObserver.assertValues(listOf(), listOf("A"), listOf("A", 10), listOf("A", 10, "C"))
		}
	}
}
