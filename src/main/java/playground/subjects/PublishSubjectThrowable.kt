package playground.subjects

import io.reactivex.Completable
import io.reactivex.subjects.PublishSubject

/**
 * This is a Rx trick used to be able to re-use the PublishSubject so we can test retries/repeats in Rx.
 *
 * The Rx chain errors downstream and disposes upstream. Because of that, the subject has not reached a terminal state (complete, error)
 * so it can be subscribed onto again.
 *
 * REMEMBER: A Subject that Completes or Errors becomes unusable and future subscriptions in an attempt to emit values will not work.
 *
 * A Subject that is disposed though will still work fine and future subscriptions in an attempt to emit values to the Subject `.onNext(..)` w
 */

class PublishSubjectThrowable {

    val onlyDisposesDoesntErrorPublishSubject = PublishSubject.create<Throwable>()

    val completable = onlyDisposesDoesntErrorPublishSubject.flatMapCompletable { Completable.error(it) }
}