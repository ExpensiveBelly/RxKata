package workshop

import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit


class Workshop {

	// Produces an Observable of Unit
	// For instance producingUnits(5) -> [Unit, Unit, Unit, Unit, Unit]
	fun producingUnits(num: Int): Observable<Unit> = Observable.just(Unit).repeat(num.toLong())

	// Should transform Unit's to toggled boolean value starting from true
	// For instance (Unit, Unit, Unit, Unit).toNextNumbers() -> [true, false, true, false]
	fun Observable<Unit>.toToggle(): Observable<Boolean> = scan(false) { toggle, _ -> !toggle }.skip(1)

	// Adds a delay of time `timeMillis` between elements
	fun <T> Observable<T>.delayEach(timeMillis: Long): Observable<T> = delay(timeMillis, TimeUnit.MILLISECONDS)

	// Should transform Unit's to next numbers startling from 1
	// For instance (Unit, Unit, Unit, Unit).toNextNumbers() -> [1, 2, 3, 4]
	fun Observable<Unit>.toNextNumbers(): Observable<Int> = scan(0) { number, _ -> number + 1 }.skip(1)

	// Produces not only elements, but the whole history till now
	// For instance (1, "A", 'C').withHistory() -> [[], [1], [1, A], [1, A, C]]
	fun <T> Observable<T>.withHistory(): Observable<List<T>> = scan(emptyList<T>()) { acc, element -> acc + element }
}