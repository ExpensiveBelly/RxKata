//package playground
//
//import io.reactivex.Observable
//import io.reactivex.disposables.CompositeDisposable
//import io.reactivex.rxkotlin.plusAssign
//import io.reactivex.rxkotlin.subscribeBy
//import io.reactivex.schedulers.Schedulers
//import javax.xml.ws.http.HTTPException
//
//
//class BasketItemsExerciseFirstApproach(val sessionApi: SessionApi,
//                val basketApi: BasketApi,
//                val productsApi: ProductsApi,
//                val view: ShoppingBasketView) {
//
//    val compositeDisposable: CompositeDisposable = CompositeDisposable()
//
//    fun attach() {
//        compositeDisposable += view.initialTrigger
//                .flatMapObservable {
//                    sessionApi.login("username", "password")
//                            .subscribeOn(Schedulers.io())
//                            .retry { times, throwable ->
//                                times < 3 && throwable.toConnectionError() == ConnectionError(ConnectionErrorType.NETWORK)
//                            }
//                            .doOnError { view.displayError(if (it.toConnectionError() == ConnectionError(ConnectionErrorType.SESSION_INVALID)) ErrorType.Other else ErrorType.Network(true)) }
//                            .cache()
//                            .flatMapObservable { sessionTo ->
//                                basketApi.getBaskets(sessionTo.secretKey)
//                                        .flattenAsObservable { it }
//                                        .flatMap({ basketTO ->
//                                            productsApi.productsSingle(sessionTo.secretKey, basketTO.id)
//                                                    .subscribeOn(Schedulers.io())
//                                                    .flatMapObservable { products ->
//                                                        Observable.just(listOf(BasketItem(basketTO.id, basketTO.name, products.map { it.toDomain() })))
//                                                    }
//                                        }, 10)
//                            }
//                }.subscribeBy {
//                    view.displayBaskets(it)
//                }
//    }
//
//    fun detach() {
//        compositeDisposable.clear()
//    }
//}
//
//private fun Throwable.toConnectionError(): ConnectionError {
//    return when (this) {
//        is HTTPException -> if (statusCode == 401) ConnectionError(ConnectionErrorType.SESSION_INVALID) else ConnectionError(ConnectionErrorType.NETWORK)
//        else -> {
//            ConnectionError(ConnectionErrorType.NETWORK)
//        }
//    }
//}
//
//private fun ProductTO.toDomain() = Product(this.id, this.name,
//        when (type) {
//            "FRUIT" -> ProductType.FRUIT
//            "MEAT" -> ProductType.MEAT
//            "FISH" -> ProductType.FISH
//            "VEGETABLES" -> ProductType.VEGETABLES
//            "UNKNOWN" -> ProductType.UNKNOWN
//            else -> {
//                ProductType.UNKNOWN
//            }
//        })
