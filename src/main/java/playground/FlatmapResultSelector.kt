package playground

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

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
        return Observable.just("Will")
                .observeOn(Schedulers.io())
                .flatMap({ name ->
                    updateUserName.execute(name) //returns another Observable<Result> after calling the API
                }, {
                    //this is executed after resolving the last observable emitted, so the result is ready.
                    name: String, apiResult: Result<String, Throwable> ->
                    Pair(name, apiResult)
                })
    }

}
