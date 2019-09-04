package operators.types

import io.reactivex.Observable
import io.reactivex.Scheduler
import java.math.BigDecimal
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.math.sin

class TradingPlatform {

    fun prices(scheduler: Scheduler): Observable<BigDecimal> {
        return Observable
                .interval(50, MILLISECONDS, scheduler)
                .flatMap { randomDelay(it) }
                .map { randomStockPrice(it) }
                .map { BigDecimal.valueOf(it) }
    }

    private fun randomDelay(x: Long): Observable<Long> {
        return Observable
                .just(x)
                .delay((Math.random() * 100).toLong(), MILLISECONDS)
    }

    private fun randomStockPrice(x: Long): Double {
        return 100.0 + Math.random() * 10 + sin(x / 100.0) * 60.0
    }


}
