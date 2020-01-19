package playground

import arrow.core.None
import arrow.core.Option
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import playground.callback.SharedPreferences
import playground.callback.WrapCallbacksUsingRx

class WrapCallbacksUsingRxTest {

    private val mockSharedPreferences = mock<SharedPreferences<String, String>> {}

    private val wrapCallbacksUsingRx =
        WrapCallbacksUsingRx(mockSharedPreferences)

    @Test
    fun `should register when subscribing and unregister when disposing`() {
        val testObserver = wrapCallbacksUsingRx.observe("").test()

        verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(any())

        testObserver.dispose()

        verify(mockSharedPreferences).unregisterOnSharedPreferenceChangeListener(any())
    }

    @Test
    fun `should get a None if the key to observe does not exist in the map`() {
        val key = "key"
        val sharedPreferences = SharedPreferences<String, String>()
        val wrapCallbacksUsingRx = WrapCallbacksUsingRx(sharedPreferences)

        val testObserver = wrapCallbacksUsingRx.observe(key).test()

        testObserver.assertValue(None)
    }

    @Test
    fun `should get a None if the key to observe exists in the map`() {
        val key = "key"
        val value = "value"
        val sharedPreferences = SharedPreferences<String, String>()
        val wrapCallbacksUsingRx = WrapCallbacksUsingRx(sharedPreferences)

        sharedPreferences.map += key to value

        val testObserver = wrapCallbacksUsingRx.observe(key).test()

        testObserver.assertValue(Option(value))
    }

    @Test
    fun `should get the values from the map when observing`() {
        val key = "key"
        val value1 = "value1"
        val value2 = "value2"
        val sharedPreferences = SharedPreferences<String, String>()
        val wrapCallbacksUsingRx = WrapCallbacksUsingRx(sharedPreferences)

        sharedPreferences.map += key to value1

        val testObserver = wrapCallbacksUsingRx.observe(key).test()

        sharedPreferences.map += key to value2

        testObserver.assertValues(Option(value1), Option(value2))
    }

    @Test
    fun `should get only the latest value from the map from when observing begins`() {
        val key = "key"
        val value1 = "value1"
        val value2 = "value2"
        val sharedPreferences = SharedPreferences<String, String>()
        val wrapCallbacksUsingRx = WrapCallbacksUsingRx(sharedPreferences)

        sharedPreferences.map += key to value1
        sharedPreferences.map += key to value2

        val testObserver = wrapCallbacksUsingRx.observe(key).test()

        testObserver.assertValues(Option(value2))
    }
}