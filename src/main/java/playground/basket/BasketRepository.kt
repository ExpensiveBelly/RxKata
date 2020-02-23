package playground.basket

import com.pacoworks.komprehensions.rx2.doFlatMap
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import utils.zip

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

    private fun Single<List<BasketTO>>.fetchGetProductsKomprehensions(sessionKey: String) =
            doFlatMap(
                    { flattenAsObservable { it } },
                    { basketTO -> Observable.fromIterable(basketTO.productIds) },
                    { _, productId ->
                        productsApi.getProducts(sessionKey, productId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.computation())
                                .doOnError { reportIfSessionInvalid() }
                                .map { it.toProduct() }
                                .toObservable()
                                .toList()
                                .toObservable()
                    },
                    { basketTO, _, products -> Observable.just(BasketItem(basketTO.id, basketTO.name, products)) })
                    .toList()


    private fun Single<List<BasketTO>>.fetchGetProductsNoConcurrencyLimit(sessionKey: String): Single<List<BasketItem>> =
            flatMap { basketTOs ->
                zip(basketTOs.map { basketTO: BasketTO ->
                    zip(basketTO.productIds.map { productId ->
                        productsApi.getProducts(sessionKey, productId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.computation())
                                .doOnError { reportIfSessionInvalid() }
                                .map { it.toProduct() }
                    }).map { BasketItem(basketTO.id, basketTO.name, it) }
                })
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