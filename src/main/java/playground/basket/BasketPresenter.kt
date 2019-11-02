package playground.basket

import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import playground.extensions.mainScheduler

class BasketPresenter(private val basketRepository: BasketRepository,
                      private val view: ShoppingBasketView) {

    private var disposable: Disposable = Disposables.empty()

    fun attach() {
        disposable = basketRepository.basketObservable
                .doOnNext { view.displayBaskets(it) }
                .doOnError {
                    when (it) {
                        is ConnectionError -> view.displayError(ErrorType.Network(it.errorType == ConnectionErrorType.NETWORK))
                        else -> view.displayError(ErrorType.Other)
                    }
                }
//                .retry()
                .observeOn(mainScheduler)
                .subscribe()
    }

    fun detach() {
        disposable.dispose()
    }
}