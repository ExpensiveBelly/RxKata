package playground.twitter

import org.junit.Test

class TwitterExerciseTest {

    private val twitter = TwitterExercise()

    @Test
    fun `follow and then unfollow`() {
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
}