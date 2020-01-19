package playground.callback

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