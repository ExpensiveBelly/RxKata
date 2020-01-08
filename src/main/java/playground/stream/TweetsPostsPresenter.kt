package playground.stream

import com.news.ConnectionError
import com.news.ConnectionErrorType
import com.news.InfoType
import com.news.InfoView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import playground.extensions.mainScheduler

class TweetsPostsPresenter(private val repository: TweetsPostsRepository) {

    private val compositeDisposable = CompositeDisposable()

    fun attach(view: InfoView) {

        compositeDisposable += repository.tweetsAndPostsStream
            .retryWhen { errors ->
                errors.takeWhile { it is ConnectionError && it.errorType == ConnectionErrorType.DISCONNECTED }
                    .observeOn(mainScheduler)
                    .doOnNext { view.displayConnectionError((it as ConnectionError).errorType) }
                    .observeOn(Schedulers.computation())
                    .flatMap { Observable.just(Unit) }
            }.subscribe()

        compositeDisposable += view.type
            .distinctUntilChanged()
            .switchMap { newsType ->
                when (newsType) {
                    InfoType.TWEETS -> repository.aggregatedTweetsObservableEager
                    InfoType.FACEBOOK_POSTS -> repository.aggregatedFacebookPostsObservableEager
                }
            }
            .observeOn(mainScheduler)
            .onErrorResumeNext(Function { throwable ->
                if (throwable is ConnectionError && throwable.errorType == ConnectionErrorType.INVALID_DATA) {
                    view.displayConnectionError(throwable.errorType) //We display an error but we continue the execution
                    Observable.just(emptyList())
                } else throw throwable
            })
            .subscribeBy(
                onNext = { view.displayItems(it) },
                onError = { if (it is ConnectionError) view.displayConnectionError(it.errorType) else throw it })
    }

    fun detach() {
        compositeDisposable.clear()
    }
}