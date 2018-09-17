package playground

import io.reactivex.Single

sealed class ErrorType {
    data class Network(val isConnected: Boolean) : ErrorType()

    object Other : ErrorType()
}

interface ShoppingBasketView {

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
    fun productsSingle(secretKey: String, id: String): Single<ProductTO>
}

data class ProductTO(val id: String, val name: String, val type: String)

enum class ConnectionErrorType {
    SESSION_INVALID,
    NETWORK
}

data class ConnectionError(val errorType: ConnectionErrorType) : Error()