package playground.maybe

import io.reactivex.Maybe
import io.reactivex.Single

/**
 * What's the difference between `flatMapSingle` and `flatMapSingleElement`?
 * It's in the documentation but because no one reads it I wrote a test for it:
 *
 * `flatMapSingle` -> When this Maybe completes a {@link NoSuchElementException} will be thrown.
 * `flatMapSingleElement` -> When this Maybe just completes the resulting {@code Maybe} completes as well.
 */

class MaybeFlatmapVariants {

    val flatMapSingle = Maybe.empty<Int>()
        .flatMapSingle { Single.just(1) }

    val flatMapSingleElement = Maybe.empty<Int>()
        .flatMapSingleElement { Single.just(1) }
}