package playground.callback

import arrow.core.Option
import arrow.core.toOption
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow


class WrapCallbacksUsingFlow(private val sharedPreferences: SharedPreferences<String, String>) {

    suspend fun observe(key: String) =
        channelFlow<Option<String>> {
            val listener = object : SharedPreferences.OnSharedPreferenceChangeListener {
                override fun onSharedPreferenceChanged() {
                    if (!channel.isClosedForSend) {
                        channel.offer(sharedPreferences.map[key].toOption())
                    }
                }
            }
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

            awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
        }
}