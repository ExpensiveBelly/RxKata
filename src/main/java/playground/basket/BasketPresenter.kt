package playground.basket

import io.reactivex.rxjava3.disposables.Disposable
import utils.mainScheduler

class BasketPresenter(private val basketRepository: BasketRepository,
                      private val view: ShoppingBasketView) {

    private var disposable: Disposable = Disposable.empty()

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