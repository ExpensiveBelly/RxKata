package playground.maybe

import io.reactivex.Maybe
import io.reactivex.Single

/**
 * How to use Maybe to only resubscribe to the stream when a condition is not met
 *
 */

class RepeatInvalidationExercise {

    fun invalidateRepeatInMaybe(completeMaybe: Boolean): Single<Boolean> {
        var list = listOf(completeMaybe) + !completeMaybe
        return Maybe.defer { Maybe.just(list.first()) }
            .doOnSuccess { list = list.drop(1) }
            .filter { !completeMaybe }
            .repeat() //This only triggers if the item doesn't pass the filter, otherwise it carries on downstream and it's converted to a Single, invalidating the `repeat`
            .firstOrError()
    }
}