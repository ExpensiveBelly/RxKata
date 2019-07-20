package playground

import io.reactivex.Observable
import org.junit.Test
import java.io.IOException


class AutoConnectTest {

    @Test
    fun autoconnect_does_not_deliver_the_error_till_there_is_a_subscriber() {
        val observable = Observable.fromCallable {
            println("Throwing Exception")
            throw IOException()
        }.replay(1).autoConnect(0)

        Thread.sleep(500)

        println("Making assertion")
        observable.test().assertError(IOException::class.java)
    }
}