package playground.twitter

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.time.Instant

typealias UserId = Int
typealias TweetId = Int
typealias FollowerId = Int
typealias FolloweeId = Int

/**
Design a simplified version of Twitter where users can post tweets,
follow/unfollow another user and is able to see the 10 most recent tweets in the user's news feed.

Your design should support the following methods:

postTweet(userId, tweetId): Compose a new tweet. get News Feed(user Id): Retrieve the 10 most recent tweet ids in the user's news feed. Each item in the news feed must be posted by users who the user followed or by the user herself. Tweets must be ordered from most recent to least recent.
follow(followerId, followed): Follower follows a followee.
unfollow(followerId, followeeId): Follower unfollow a followee.
 */

class TwitterExercise {

    data class TweetInfo(val userId: UserId, val tweetId: TweetId, val instant: Instant)

    private val tweets: BehaviorSubject<List<TweetInfo>> = BehaviorSubject.create()
    private val followers: BehaviorSubject<Map<FollowerId, Set<FolloweeId>>> =
        BehaviorSubject.create()

    fun postTweet(userId: UserId, tweetId: TweetId) {
        tweets.onNext(listOf(TweetInfo(userId, tweetId, Instant.now())) + tweets.value.orEmpty())
    }

    fun getNewsFeed(userId: UserId) =
        Observables.combineLatest(
            tweets,
            followers.flatMapMaybe { map -> map[userId]?.let { Maybe.just(it) } ?: Maybe.empty() }
                .startWithItem(emptySet())
                .distinctUntilChanged()
        ).map { (tweetsForUserId, followers) ->
            tweetsForUserId.asSequence()
                .filter { it.userId == userId || followers.contains(it.userId) }
                .sortedByDescending { it.instant }
                .map { it.tweetId }
                .take(10)
                .toList()
        }.distinctUntilChanged()

    fun follow(followerId: FollowerId, followeeId: FolloweeId) {
        val map = followers.value.orEmpty()
        val existingFolloweeList = map[followerId].orEmpty()
        followers.onNext(map + (followerId to existingFolloweeList + followeeId))
    }

    fun unfollow(followerId: FollowerId, followeeId: FolloweeId) {
        val map = followers.value ?: emptyMap()
        map[followerId]?.let { existingFolloweeList ->
            followers.onNext(map + (followerId to existingFolloweeList - followeeId))
        }
    }
}