package operators

import io.reactivex.Scheduler
import operators.types.TradingPlatform
import java.util.concurrent.TimeUnit


class TradingSolutions {

    /**
     * Silence the `TradingPlatform`. We don't want the TradingPlatform to emit any values.
     */

    fun noEventEmitted(scheduler: Scheduler) = TradingPlatform().prices(scheduler).debounce(1, TimeUnit.HOURS)
}