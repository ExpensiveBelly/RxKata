package playground.stream.flow

import kotlinx.coroutines.flow.Flow
import playground.stream.InfoItem
import playground.stream.InfoType


typealias ContentId = String

/**
 * Refer to TweetsPostsExercise.kt
 */

class TweetsPostsFlowExercise {

    interface InfoFlowApi {
        suspend fun currentTweets(): InfoTO

        suspend fun currentFacebookPosts(): InfoTO

        val stream: Flow<InfoItemTO>

        suspend fun getDetails(id: ContentId): InfoItemDetailsTO
    }

    data class InfoTO(val items: List<InfoItemTO>)

    data class InfoItemTO(val id: ContentId, val type: String)

    data class InfoItemDetailsTO(val title: String, val date: String)

    enum class ConnectionErrorType {
        UNKNOWN,
        DISCONNECTED,
        INVALID_DATA
    }

    data class ConnectionError(val errorType: ConnectionErrorType) : Error()

    interface InfoView {
        val type: Flow<InfoType>

        fun displayItems(items: List<InfoItem>)

        fun displayConnectionError(type: ConnectionErrorType)
    }
}