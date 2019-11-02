package playground.stream

import com.news.ConnectionError
import com.news.ConnectionErrorType
import com.news.InfoType
import com.news.InfoView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
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
                .switchMap { newsType ->
                    when (newsType) {
                        InfoType.TWEETS -> repository.aggregatedTweetsObservable
                        InfoType.FACEBOOK_POSTS -> repository.aggregatedFacebookPostsObservable
                    }
                }
                .observeOn(mainScheduler)
                .subscribeBy(onNext = { view.displayItems(it) })
    }

    fun detach() {
        compositeDisposable.clear()
    }
}