package extensions

import hu.akarnokd.rxjava2.operators.ObservableTransformers
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


class FlatmapDropExercise(scheduler: Scheduler = Schedulers.computation()) {

    private val atomicInteger = AtomicInteger()

    private val fakeNetworkRequest = Single.defer {
        Single.just(atomicInteger.getAndIncrement()).delay(1, TimeUnit.SECONDS, scheduler)
    }.toObservable().replay(1).refCount().firstOrError()

    fun Observable<Unit>.multipleClicksDoesNotTriggerNewRequestWithoutFlatmapDrop() =
            flatMapSingle { fakeNetworkRequest }.firstOrError()

    fun Observable<Unit>.multipleClicksDoesNotTriggerNewRequestWithFlatmapDrop() =
            compose(ObservableTransformers.flatMapDrop<Unit, Int> { fakeNetworkRequest.toObservable() })
}