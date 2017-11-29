package operators

import io.reactivex.Observable
import io.reactivex.Scheduler
import operators.types.TradingPlatform
import java.math.BigDecimal
import java.util.concurrent.TimeUnit


class TradingSolutions {

    /**
     * Silence the `TradingPlatform`. We don't want the TradingPlatform to emit any values.
     */

    fun noEventEmitted(scheduler: Scheduler): Observable<BigDecimal> = TradingPlatform().prices(scheduler).debounce(1, TimeUnit.HOURS)
}