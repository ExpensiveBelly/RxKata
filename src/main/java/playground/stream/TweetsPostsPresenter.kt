package playground.stream

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import onErrorResume
import utils.mainScheduler

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
            .onErrorResume { throwable ->
                if (throwable is ConnectionError && throwable.errorType == ConnectionErrorType.INVALID_DATA) {
                    view.displayConnectionError(throwable.errorType) //We display an error but we continue the execution
                    Observable.just(emptyList())
                } else throw throwable
            }.subscribeBy { view.displayItems(it) }
    }

    fun detach() {
        compositeDisposable.clear()
    }
}