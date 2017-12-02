package operators
import io.reactivex.Observable
import io.reactivex.Scheduler
import java.math.BigDecimal


class TradingExercises {

    /**
     * Silence the `TradingPlatform`. We don't want the TradingPlatform to emit any values.
     */

    fun noEventEmitted(scheduler: Scheduler): Observable<BigDecimal> {
        throw NotImplementedError()
    }
}