package playground

import io.reactivex.Single
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
                        .flatMap { basketTOs ->
                            Single.zip(basketTOs.map { basketTO ->
                                Single.zip(basketTO.productIds.map { productId ->
                                    productsApi.productsSingle(sessionKey, productId)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(Schedulers.computation())
                                            .doOnError { reportIfSessionInvalid() }
                                            .map { it.toProduct() }
                                }) { list -> list.map { it as Product } }.map { BasketItem(basketTO.id, basketTO.name, it) }
                            }) { list -> list.map { it as BasketItem } }
                        }
            }.replay(1).refCount()

    private fun reportIfSessionInvalid(): (Throwable) -> Unit =
            { if (it is ConnectionError && it.errorType == ConnectionErrorType.SESSION_INVALID) sessionRepository.reportSessionInvalid() }
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