package playground.basket

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers


class BasketRepository(private val sessionRepository: SessionRepository,
                       private val basketApi: BasketApi,
                       private val productsApi: ProductsApi) {

    val basketObservable = sessionRepository
            .sessionObservable
            .switchMapSingle { sessionKey ->
                basketApi.getBaskets(sessionKey)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .doOnError { reportIfSessionInvalid() }
                        .compose(fetchGetProductsNoConcurrencyLimit(sessionKey))
            }
            .replay(1).refCount()

    private fun reportIfSessionInvalid(): (Throwable) -> Unit =
            { if (it is ConnectionError && it.errorType == ConnectionErrorType.SESSION_INVALID) sessionRepository.reportSessionInvalid() }

    /**
     * Limits concurrency to 10 threads in parallel
     */

    private fun fetchGetProductsMaxConcurrency(sessionKey: String, maxConcurrency: Int = 10): SingleTransformer<List<BasketTO>, List<BasketItem>> {
        return SingleTransformer { basketToList ->
            basketToList.flattenAsObservable { it }
                    .flatMapSingle { basketTO ->
                        Observable.fromIterable(basketTO.productIds)
                                .flatMap({ productId ->
                                    productsApi.getProducts(sessionKey, productId)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(Schedulers.computation())
                                            .doOnError { reportIfSessionInvalid() }
                                            .map { it.toProduct() }
                                            .toObservable()
                                }, maxConcurrency)
                                .toList()
                                .map { BasketItem(basketTO.id, basketTO.name, it) }
                    }.toList()
        }
    }

    private fun fetchGetProductsNoConcurrencyLimit(sessionKey: String): SingleTransformer<List<BasketTO>, List<BasketItem>> {
        return SingleTransformer { basketToList ->
            basketToList.flatMap { basketTOs ->
                Single.zip(basketTOs.map { basketTO ->
                    Single.zip(basketTO.productIds.map { productId ->
                        productsApi.getProducts(sessionKey, productId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.computation())
                                .doOnError { reportIfSessionInvalid() }
                                .map { it.toProduct() }
                    }) { list -> list.map { it as Product } }.map { BasketItem(basketTO.id, basketTO.name, it) }
                }) { list -> list.map { it as BasketItem } }
            }
        }
    }
}


fun ProductTO.toProduct() = Product(this.id, this.name,
        when (type) {
            "FRUIT" -> ProductType.FRUIT
            "MEAT" -> ProductType.MEAT
            "FISH" -> ProductType.FISH
            "VEGETABLES" -> ProductType.VEGETABLES
            "UNKNOWN" -> ProductType.UNKNOWN
            else -> {
                ProductType.UNKNOWN
            }
        })