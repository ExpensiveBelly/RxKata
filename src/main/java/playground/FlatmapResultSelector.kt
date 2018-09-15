package playground

import io.reactivex.Observable

/**
 * https://medium.com/@douglas.iacovelli/rxjava-kotlin-keep-the-original-value-in-a-flatmap-bbd6a6974a99
 */

class FlatmapResultSelector {

    private val updateUserName: UpdateUserName = UpdateUserName()

    class UpdateUserName {
        fun execute(name: String): Observable<Result<String, Throwable>> =
                Observable.just(Result.Success(name))
    }

    fun `flatmap with result selector`(): Observable<Pair<String, Result<String, Throwable>>> {
        throw NotImplementedError()
    }

}
