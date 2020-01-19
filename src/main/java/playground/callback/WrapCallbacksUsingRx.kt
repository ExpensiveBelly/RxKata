package playground.callback

import arrow.core.toOption
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class WrapCallbacksUsingRx(private val sharedPreferences: SharedPreferences<String, String>) {

    fun observe(key: String) = Observable.using(
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
}