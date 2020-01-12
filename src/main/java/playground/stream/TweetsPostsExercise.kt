package playground.stream

import io.reactivex.Observable
import io.reactivex.Single

/**
 * Given the following classes, implement a way for the `InfoView` to `displayItems`.
 * Bear in mind the following constraints:
 * 1. The view can subscribe to either Tweets or Facebook Posts, we don't want to be subscribed to the two of them.
 * 2. The `currentTweets` / `currentFacebookPosts` need to be displayed before we subscribe to the stream, as the stream just add deltas to the currentTweets/currentFacebookPosts
 * 3. `currentTweets/currentFacebookPosts/ return InfoTO, which contain only the Ids, but not the `InfoItem`. In order to transform it we need to get the details for each id.
 * 3. Threading (parallelism whenever possible)
 * 4. Error handling (stream disconnected, getDetails failure, retries)
 * 5. The details for ids don't change, so it would be good to cache them
 * 6. Let's assume multiple rows in a list are subscribed to either Tweets or Facebook Posts (there's a toggle for each row). We don't want to create different
 * streams for each row, but share the streams whenever possible for network efficiency.
 */

interface InfoApi {
    val currentTweets: Single<InfoTO>

    val currentFacebookPosts: Single<InfoTO>

    val stream: Observable<InfoItemTO>

    fun getDetails(id: Id): Single<InfoItemDetailsTO>
}

data class InfoTO(val items: List<InfoItemTO>)

data class InfoItemTO(val id: Id, val type: String)

typealias Id = String

data class InfoItemDetailsTO(val title: String, val date: String)

enum class ConnectionErrorType {
    UNKNOWN,
    DISCONNECTED,
    INVALID_DATA
}

data class ConnectionError(val errorType: ConnectionErrorType) : Error()

interface InfoView {
    val type: Observable<InfoType>

    fun displayItems(items: List<InfoItem>)

    fun displayConnectionError(type: ConnectionErrorType)
}

data class InfoItem(
        val id: String,
        val title: String,
        val date: String
)

enum class InfoType {
    TWEETS,
    FACEBOOK_POSTS
}

