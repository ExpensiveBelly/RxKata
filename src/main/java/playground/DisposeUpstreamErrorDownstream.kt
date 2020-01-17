package playground

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface StreamErrorMockForTesting {
    fun beforeMapOnNext()
    fun beforeMapOnDispose()
    fun beforeMapOnError()

    fun afterMapOnNext()
    fun afterMapOnDispose()
    fun afterMapOnError()
}

interface StreamCompleteMockForTesting {

    fun beforeFlatMap()
    fun afterFlatMap()
}

class DisposeUpstreamErrorDownstream(
    streamErrorMockForTesting: StreamErrorMockForTesting,
    streamCompleteMockForTesting: StreamCompleteMockForTesting
) {

    private val thisSubjectGetsIgnored = PublishSubject.create<Unit>()

    val streamErrors = Observable.defer {
        Observable.just(1, 2, 3)
            .doOnNext { streamErrorMockForTesting.beforeMapOnNext() }
            .doOnDispose { streamErrorMockForTesting.beforeMapOnDispose() }
            .doOnError { streamErrorMockForTesting.beforeMapOnError() }
            .map { throw IllegalStateException() }
            .flatMap { thisSubjectGetsIgnored }
            .doOnNext { streamErrorMockForTesting.afterMapOnNext() }
            .doOnError { streamErrorMockForTesting.afterMapOnError() }
            .doOnDispose { streamErrorMockForTesting.afterMapOnDispose() }
    }

    val subject = PublishSubject.create<Unit>()

    val streamCompletes = Observable.defer {
        Observable.just(1, 2, 3)
            .doOnComplete { streamCompleteMockForTesting.beforeFlatMap() }
            .flatMap { subject }
            .doOnComplete { streamCompleteMockForTesting.afterFlatMap() }
    }
}