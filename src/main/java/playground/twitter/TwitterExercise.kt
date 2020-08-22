package playground.twitter

import io.reactivex.rxjava3.core.Observable

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
    fun getFollowees(followerId: FollowerId): Observable<Set<FolloweeId>>
    fun follow(followeeId: FolloweeId, followerId: FollowerId)
    fun unfollow(followerId: FollowerId, followeeId: FolloweeId)
}

class TwitterExercise : ITwitterExercise {
    override fun postTweet(userId: UserId, tweetId: TweetId) {
        TODO("Not yet implemented")
    }

    override fun getNewsFeed(userId: UserId): Observable<List<TweetId>> {
        TODO("Not yet implemented")
    }

    override fun getFollowees(followerId: FollowerId): Observable<Set<FolloweeId>> {
        TODO("Not yet implemented")
    }

    override fun follow(followeeId: FolloweeId, followerId: FollowerId) {
        TODO("Not yet implemented")
    }

    override fun unfollow(followerId: FollowerId, followeeId: FolloweeId) {
        TODO("Not yet implemented")
    }
}