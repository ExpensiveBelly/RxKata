package playground.twitter

import it.droidcon.testingdaggerrxjava.rx3.TestSchedulerRule
import org.junit.Rule
import org.junit.Test

class TwitterExerciseTest {

    @get:Rule
    val rule = TestSchedulerRule()

    private val twitter = TwitterExercise()

    @Test
    fun `follow and then unfollow should show tweets posted by user and followers`() {
        twitter.postTweet(1, 5)

        val test = twitter.getNewsFeed(1).test()

        twitter.follow(2, 1)
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
        twitter.getNewsFeed(1).test()

        twitter.postTweet(1, 5)

        twitter.getNewsFeed(2).test().assertValue(emptyList())
    }

    @Test
    fun `if only showing tweets from followers then if unfollow not showing any tweets`() {
        twitter.postTweet(1, 5)
        twitter.follow(1, 2)
        twitter.unfollow(2, 1)

        val test = twitter.getNewsFeed(2).test()

        test.assertValues(emptyList())
    }

    @Test
    fun `should be able to get the news feed from more than one user id`() {
        twitter.follow(2, 1)
        twitter.follow(1, 2)

        val test1 = twitter.getNewsFeed(1).test()
        val test2 = twitter.getNewsFeed(2).test()

        twitter.postTweet(1, 5)
        twitter.postTweet(2, 6)

        test1.assertValues(emptyList(), listOf(5), listOf(6, 5))
        test2.assertValues(emptyList(), listOf(5), listOf(6, 5))

        twitter.unfollow(1, 2)
        twitter.unfollow(2, 1)

        test1.assertValues(emptyList(), listOf(5), listOf(6, 5), listOf(5))
        test2.assertValues(listOf(5), listOf(6, 5), listOf(6))
    }

    @Test
    fun `should limit the news feed to 10 tweets`() {
        for (i in 0 until 11) {
            twitter.postTweet(1, i)
        }

        val test = twitter.getNewsFeed(1).test()
        val firstList = (10 downTo 1).map { it }.toList()
        test.assertValue(firstList)

        twitter.postTweet(1, 11)

        test.assertValueCount(2)
        test.assertValues(firstList, (11 downTo 2).map { it }.toList())
    }
}