package playground

import io.reactivex.Observable

interface MockForTesting {
    fun beforeMapOnNext()
    fun beforeMapOnDispose()
    fun beforeMapOnError()

    fun afterMapOnNext()
    fun afterMapOnDispose()
    fun afterMapOnError()
}

class DisposeUpstreamErrorDownstream(mockForTesting: MockForTesting) {

    val stream = Observable.defer {
        Observable.just(1, 2, 3)
            .doOnNext { mockForTesting.beforeMapOnNext() }
            .doOnDispose { mockForTesting.beforeMapOnDispose() }
            .doOnError { mockForTesting.beforeMapOnError() }
            .map { throw IllegalStateException() }
            .doOnNext { mockForTesting.afterMapOnNext() }
            .doOnError { mockForTesting.afterMapOnError() }
            .doOnDispose { mockForTesting.afterMapOnDispose() }
    }
}