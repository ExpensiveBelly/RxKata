package playground.callback

import arrow.core.toOption
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlin.properties.Delegates

class SharedPreferences<K, V> {

    var listeners = emptyList<OnSharedPreferenceChangeListener>()

    var map by Delegates.observable(emptyMap<K, V>(), { _, _, _ ->
        listeners.forEach { it.onSharedPreferenceChanged() }
    })

    fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        synchronized(listeners) {
            listeners += listener
        }
    }

    fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        synchronized(listeners) {
            listeners -= listener
        }
    }

    interface OnSharedPreferenceChangeListener {
        fun onSharedPreferenceChanged()
    }
}

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

            ObservableResources(keyChangesSubject, listener)
        },
        { (keyChangesSubject, _) ->
            keyChangesSubject.filter { it == key }.startWith(key).map { (sharedPreferences.map[key]).toOption() }
        },
        { (_, listener) ->
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        })

    private data class ObservableResources(
        val keyChangesSubject: PublishSubject<String>,
        val listener: SharedPreferences.OnSharedPreferenceChangeListener
    )
}