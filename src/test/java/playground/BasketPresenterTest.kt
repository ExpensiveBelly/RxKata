package playground

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Single
import it.droidcon.testingdaggerrxjava.TrampolineSchedulerRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BasketPresenterTest {

    @get:Rule
    val schedulerRule = TrampolineSchedulerRule()

    val secretKey = "1234"

    val sessionApi = mock<SessionApi> {
        on { login(any(), any()) } doReturn Single.just(SessionTO(true, secretKey))
    }

    val productId1 = "productId1"
    val productId2 = "productId2"
    val productId3 = "productId3"
    val basketId = "1"
    val basketApi = mock<BasketApi> {
        on { getBaskets(secretKey) } doReturn Single.just(listOf(BasketTO(basketId, "My shopping list",
                listOf(productId1, productId2, productId3))))
    }

    val productTO = ProductTO(productId1, "Lettuce", "VEGETABLES")
    val productTO1 = ProductTO(productId2, "Diced Chicken", "MEAT")
    val productTO2 = ProductTO(productId3, "Banana", "FRUIT")
    val productsApi = mock<ProductsApi> {
        on { productsSingle(secretKey, productId1) } doReturn Single.just(productTO)
        on { productsSingle(secretKey, productId2) } doReturn Single.just(productTO1)
        on { productsSingle(secretKey, productId3) } doReturn Single.just(productTO2)
    }

    val view = mock<ShoppingBasketView> {}

    private val presenter: BasketPresenter = BasketPresenter(
            BasketRepository(
                    SessionRepository(sessionApi, "username", "password"),
                    basketApi, productsApi), view)

    @Before
    fun setUp() {
        presenter.attach()
    }

    @Test
    fun should_display_list_of_basket_items() {
        verify(view).displayBaskets(listOf(BasketItem(basketId, "My shopping list",
                listOf(productTO.toPresentation(),
                        productTO1.toPresentation(),
                        productTO2.toPresentation()))))
    }

    @After
    fun tearDown() {
        presenter.detach()
    }
}