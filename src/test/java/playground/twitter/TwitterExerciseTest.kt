package playground.twitter

import org.junit.Test

class TwitterExerciseTest {

    private val twitter = TwitterExercise()

    @Test
    fun `follow and then unfollow should show tweets posted by user and followers`() {
        twitter.postTweet(1, 5)

        val test = twitter.getNewsFeed(1).test()

        twitter.follow(1, 2)
        twitter.postTweet(2, 6)
        twitter.unfollow(1, 2)

        test.assertValues(listOf(5), listOf(6, 5), listOf(5))
    }

    @Test
    fun `post tweets posted by the user`() {
        twitter.postTweet(1, 5)
        twitter.postTweet(1, 6)
        twitter.postTweet(1, 7)

        val test = twitter.getNewsFeed(1).test()
        test.assertValue(listOf(7, 6, 5))

        twitter.postTweet(1, 8)

        test.assertValues(listOf(7, 6, 5), listOf(8, 7, 6, 5))
    }

    @Test
    fun `if nothing posted for that userId then emptyList`() {
        twitter.getNewsFeed(1).test().assertNoValues()

        twitter.postTweet(1, 5)

        twitter.getNewsFeed(2).test().assertValue(emptyList())
    }

    @Test
    fun `if only showing tweets from followers then if unfollow not showing any tweets`() {
        twitter.postTweet(1, 5)
        twitter.follow(2, 1)
        twitter.unfollow(2, 1)

        val test = twitter.getNewsFeed(2).test()

        test.assertValues(emptyList())
    }

    @Test
    fun `should be able to get the news feed from more than one user id`() {
        twitter.follow(1, 2)
        twitter.follow(2, 1)

        val test1 = twitter.getNewsFeed(1).test()
        val test2 = twitter.getNewsFeed(2).test()

        twitter.postTweet(1, 5)
        twitter.postTweet(2, 6)

        test1.assertValues(listOf(5), listOf(6, 5))
        test2.assertValues(listOf(5), listOf(6, 5))

        twitter.unfollow(1, 2)
        twitter.unfollow(2, 1)

        test1.assertValues(listOf(5), listOf(6, 5), listOf(5))
        test2.assertValues(listOf(5), listOf(6, 5), listOf(6))
    }
}