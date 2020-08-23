package playground.basket

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import zip

class BasketRepository(
    private val sessionRepository: SessionRepository,
    private val basketApi: BasketApi,
    private val productsApi: ProductsApi
) {

    val basketObservable = sessionRepository
        .sessionObservable
        .switchMapSingle { sessionKey ->
            basketApi.getBaskets(sessionKey)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnError { reportIfSessionInvalid() }
                .fetchGetProductsMaxConcurrency(sessionKey)
        }
        .replay(1).refCount()

    private fun reportIfSessionInvalid(): (Throwable) -> Unit =
        { if (it is ConnectionError && it.errorType == ConnectionErrorType.SESSION_INVALID) sessionRepository.reportSessionInvalid() }

    /**
     * Limits concurrency to 10 threads in parallel
     */

    private fun Single<List<BasketTO>>.fetchGetProductsMaxConcurrency(sessionKey: String, maxConcurrency: Int = 10) =
        flattenAsObservable { it }
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

    private fun Single<List<BasketTO>>.fetchGetProductsNoConcurrencyLimit(sessionKey: String): Single<List<BasketItem>> =
        flatMap { basketTOs ->
            zip(basketTOs.map { basketTO: BasketTO ->
                zip(basketTO.productIds.map { productId ->
                    productsApi.getProducts(sessionKey, productId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .doOnError { reportIfSessionInvalid() }
                        .map { it.toProduct() }
                }, defaultWhenEmpty = emptyList()).map { BasketItem(basketTO.id, basketTO.name, it) }
            }, defaultWhenEmpty = emptyList())
        }
}

fun ProductTO.toProduct() = Product(
    this.id, this.name,
    when (type) {
        "FRUIT" -> ProductType.FRUIT
        "MEAT" -> ProductType.MEAT
        "FISH" -> ProductType.FISH
        "VEGETABLES" -> ProductType.VEGETABLES
        "UNKNOWN" -> ProductType.UNKNOWN
        else -> {
            ProductType.UNKNOWN
        }
    }
)