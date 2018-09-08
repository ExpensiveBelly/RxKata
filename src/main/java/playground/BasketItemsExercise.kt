package playground

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.xml.ws.http.HTTPException

sealed class ErrorType {
    data class Network(val isConnected: Boolean) : ErrorType()

    object Other : ErrorType()
}

interface ShoppingBasketView {

    val initialTrigger: Single<Unit>

    fun displayBaskets(items: List<BasketItem>)
    fun displayError(error: ErrorType)
}

data class BasketItem(val id: BasketId, val name: String, val products: List<Product>)
data class Product(val id: ProductId, val name: String, val type: ProductType)

enum class ProductType {
    FRUIT,
    MEAT,
    FISH,
    VEGETABLES,
    UNKNOWN
}

typealias BasketId = String
typealias ProductId = String

//API Layer

interface SessionApi {
    fun login(username: String, password: String): Single<SessionTO>
    fun logout()
}

data class SessionTO(val success: Boolean, val secretKey: String)

interface BasketApi {
    fun getBaskets(secretKey: String): Single<List<BasketTO>>
}

data class BasketTO(val id: String, val name: String, val productIds: List<String>)

interface ProductsApi {
    fun productsSingle(secretKey: String, id: String): Single<List<ProductTO>>
}

data class ProductTO(val id: String, val name: String, val type: String)

enum class ConnectionErrorType {
    SESSION_INVALID,
    NETWORK
}

data class ConnectionError(val errorType: ConnectionErrorType) : Error()

class Presenter(val sessionApi: SessionApi,
                val basketApi: BasketApi,
                val productsApi: ProductsApi,
                val view: ShoppingBasketView) {

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun attach() {
        compositeDisposable += view.initialTrigger
                .flatMapObservable {
                    sessionApi.login("username", "password")
                            .subscribeOn(Schedulers.io())
                            .retry { times, throwable ->
                                times < 3 && throwable.toConnectionError() == ConnectionError(ConnectionErrorType.NETWORK)
                            }
                            .doOnError { view.displayError(if (it.toConnectionError() == ConnectionError(ConnectionErrorType.SESSION_INVALID)) ErrorType.Other else ErrorType.Network(true)) }
                            .cache()
                            .flatMapObservable { sessionTo ->
                                basketApi.getBaskets(sessionTo.secretKey)
                                        .flattenAsObservable { it }
                                        .flatMap({ basketTO ->
                                            productsApi.productsSingle(sessionTo.secretKey, basketTO.id)
                                                    .subscribeOn(Schedulers.io())
                                                    .flatMapObservable { products ->
                                                        Observable.just(listOf(BasketItem(basketTO.id, basketTO.name, products.map { it.toDomain() })))
                                                    }
                                        }, 10)
                            }
                }.subscribeBy {
                    view.displayBaskets(it)
                }
    }

    fun detach() {
        compositeDisposable.clear()
    }
}

private fun Throwable.toConnectionError(): ConnectionError {
    return when (this) {
        is HTTPException -> if (statusCode == 401) ConnectionError(ConnectionErrorType.SESSION_INVALID) else ConnectionError(ConnectionErrorType.NETWORK)
        else -> {
            ConnectionError(ConnectionErrorType.NETWORK)
        }
    }
}

private fun ProductTO.toDomain() = Product(this.id, this.name,
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
