package playground.twitter

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.Observables
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.time.Instant

/**
Design a simplified version of Twitter where users can post tweets,
follow/unfollow another user and is able to see the 10 most recent tweets in the user's news feed.

Your design should support the following methods:

postTweet(userId, tweetId): Compose a new tweet. get News Feed(user Id): Retrieve the 10 most recent tweet ids in the user's news feed.
Each item in the news feed must be posted by users who the user followed or by the user herself.
Tweets must be ordered from most recent to least recent.

follow(followerId, followed): Follower follows a followee.

unfollow(followerId, followeeId): Follower unfollow a followee.
 */

typealias UserId = Int
typealias TweetId = Int
typealias FollowerId = Int
typealias FolloweeId = Int

interface ITwitterExercise {
    fun postTweet(userId: UserId, tweetId: TweetId)
    fun getNewsFeed(userId: UserId): Observable<List<TweetId>>
    fun follow(followerId: FollowerId, followeeId: FolloweeId)
    fun unfollow(followerId: FollowerId, followeeId: FolloweeId)
}

class TwitterExercise : ITwitterExercise {

    data class TweetInfo(val userId: UserId, val tweetId: TweetId, val instant: Instant)

    private val tweets: BehaviorSubject<List<TweetInfo>> = BehaviorSubject.create()
    private val followers: BehaviorSubject<Map<FollowerId, Set<FolloweeId>>> =
        BehaviorSubject.create()

    override fun postTweet(userId: UserId, tweetId: TweetId) {
        synchronized(tweets) {
            tweets.onNext(
                listOf(
                    TweetInfo(
                        userId,
                        tweetId,
                        Instant.now()
                    )
                ) + tweets.value.orEmpty()
            )
        }
    }

    override fun getNewsFeed(userId: UserId): Observable<List<TweetId>> =
        Observables.combineLatest(tweets, userId.followees())
            .switchMapSingle { (tweetList, followeeis) ->
                Observable.fromIterable(tweetList)
                    .groupBy { it.userId }
                    .flatMap { groupedObservable ->
                        groupedObservable.filter { it.userId == userId || followeeis.contains(it.userId) }
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.computation())
                    }
                    .sorted { o1, o2 -> o2.instant.compareTo(o1.instant) }
                    .take(10)
                    .map { it.tweetId }
                    .toList()
            }.distinctUntilChanged()

    private fun UserId.followees(): Observable<Set<FolloweeId>> =
        followers.flatMapMaybe { it[this]?.let { Maybe.just(it) } ?: Maybe.empty() }
            .startWithItem(emptySet())
            .distinctUntilChanged()

    override fun follow(followerId: FollowerId, followeeId: FolloweeId) {
        synchronized(followers) {
            val map = followers.value.orEmpty()
            val existingFolloweeList = map[followerId].orEmpty()
            followers.onNext(map + (followerId to existingFolloweeList + followeeId))
        }
    }

    override fun unfollow(followerId: FollowerId, followeeId: FolloweeId) {
        synchronized(followers) {
            val map = followers.value ?: emptyMap()
            map[followerId]?.let { existingFolloweeList ->
                followers.onNext(map + (followerId to existingFolloweeList - followeeId))
            }
        }
    }
}