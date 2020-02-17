package playground.callback

import arrow.core.Option
import arrow.core.toOption
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.PublishSubject

class WrapCallbacksUsingRx(private val sharedPreferences: SharedPreferences<String, String>) {

    fun observeWithUsing(key: String) = Observable.using(
        {
            val keyChangesSubject = PublishSubject.create<String>()
            val listener = object :
                SharedPreferences.OnSharedPreferenceChangeListener {
                override fun onSharedPreferenceChanged() {
                    keyChangesSubject.onNext(key)
                }
            }
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

            Pair(keyChangesSubject, listener)
        },
        { (keyChangesSubject, _) ->
            keyChangesSubject.filter { it == key }.startWith(key).map { (sharedPreferences.map[key]).toOption() }
        },
        { (_, listener) ->
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        })

    fun observeWithEmitter(key: String) = Observable.create<Option<String>> { emitter ->
        val listener = object : SharedPreferences.OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged() {
                if (!emitter.isDisposed) emitter.onNext((sharedPreferences.map[key]).toOption())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        if (!emitter.isDisposed) emitter.onNext((sharedPreferences.map[key]).toOption())

        emitter.setDisposable(Disposables.fromAction {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(
                listener
            )
        })
    }
}