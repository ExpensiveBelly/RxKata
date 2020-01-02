package playground.maybe

import io.reactivex.Observable

class MaybeToSingle {

    val single = Observable.just(true)
        .doOnSubscribe { println("Subscribed") }
        .filter(true::equals)
        .repeat()
        .singleOrError()
}