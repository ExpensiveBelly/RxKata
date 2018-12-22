package playground.basket

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.subjects.SingleSubject
import it.droidcon.testingdaggerrxjava.TrampolineSchedulerRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BasketPresenterTest {

    @get:Rule
    val schedulerRule = TrampolineSchedulerRule()

    private val secretKey = "1234"

    private val sessionApiSubject = SingleSubject.create<SessionTO>()
    private val sessionApi = mock<SessionApi> {
        on { login(any(), any()) } doReturn sessionApiSubject
    }

    private val productId1 = "productId1"
    private val productId2 = "productId2"
    private val productId3 = "productId3"
    private val basketId = "1"
    private val getBasketSubject = SingleSubject.create<List<BasketTO>>()
    private val basketApi = mock<BasketApi> {
        on { getBaskets(secretKey) } doReturn getBasketSubject
    }

    private val productTO3 = ProductTO(productId1, "Lettuce", "VEGETABLES")
    private val productTO1 = ProductTO(productId2, "Diced Chicken", "MEAT")
    private val productTO2 = ProductTO(productId3, "Banana", "FRUIT")
    private val product1ApiSubject = SingleSubject.create<ProductTO>()
    private val product2ApiSubject = SingleSubject.create<ProductTO>()
    private val product3ApiSubject = SingleSubject.create<ProductTO>()
    private val productsApi = mock<ProductsApi> {
        on { getProducts(secretKey, productId1) } doReturn product1ApiSubject
        on { getProducts(secretKey, productId2) } doReturn product2ApiSubject
        on { getProducts(secretKey, productId3) } doReturn product3ApiSubject
    }

    private val view = mock<ShoppingBasketView> {}

    private val sessionRepository = SessionRepository(sessionApi, "username", "password")

    private val presenter: BasketPresenter = BasketPresenter(
            BasketRepository(
                    sessionRepository,
                    basketApi, productsApi), view)

    @Before
    fun setUp() {
        presenter.attach()
    }

    @Test
    fun should_display_list_of_basket_items() {
        sessionApiSubject.onSuccess((SessionTO(true, secretKey)))
        getBasketSubject.onSuccess(myBasket())

        verify(view).displayBaskets(listOf(BasketItem(basketId, "My shopping list",
                listOf(productTO1.toProduct(),
                        productTO2.toProduct(),
                        productTO3.toProduct()))))
    }

    @Test
    fun should_display_network_error_when_login_failure() {
        sessionApiSubject.onError(ConnectionError(ConnectionErrorType.NETWORK))
        getBasketSubject.onSuccess((myBasket()))

        verify(view).displayError(ErrorType.Network(true))
    }

    @Test
    fun should_display_network_error_when_getting_baskets_fails() {
        sessionApiSubject.onSuccess((SessionTO(true, secretKey)))
        getBasketSubject.onError(ConnectionError(ConnectionErrorType.NETWORK))
        product1ApiSubject.onSuccess(productTO1)
        product2ApiSubject.onSuccess(productTO2)
        product3ApiSubject.onSuccess(productTO3)

        verify(view).displayError(ErrorType.Network(true))
    }

    @Test
    fun should_display_session_failure_if_disconnected() {
        sessionApiSubject.onSuccess((SessionTO(true, secretKey)))
        getBasketSubject.onSuccess(myBasket())

        sessionRepository.reportSessionInvalid()

        verify(view).displayError(ErrorType.Other)
    }

    private fun myBasket(): List<BasketTO> {
        product1ApiSubject.onSuccess(productTO1)
        product2ApiSubject.onSuccess(productTO2)
        product3ApiSubject.onSuccess(productTO3)
        return listOf(BasketTO(basketId, "My shopping list",
                listOf(productId1, productId2, productId3)))
    }

    @After
    fun tearDown() {
        presenter.detach()
    }
}