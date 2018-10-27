package playground.basket

import io.reactivex.Single

/**
 * Given the following classes, implement a way for the `ShoppingBasketView` to `displayBaskets`.
 * Bear in mind the following constraints:
 * 1. In order to `getBaskets` we need a `secretKey`, and for that we need to be `login` (SessionApi)
 * 2. `getBaskets` return BasketTOs, which contain only the productIds, but not the `Product`. In order to get the product
 * we need to use the `ProductsApi` `productsSingle()`
 * 3. Threading (parallelism whenever possible)
 * 4. Error handling (login failure, getProducts failure, retries)
 * 5. Bear in mind that we don't want to login twice, even if we try to request the products twice. Think of a way to cache
 * the login response so it can be re-used in the stream.
 */

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