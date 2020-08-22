package playground.twitter

import it.droidcon.testingdaggerrxjava.rules.CoroutineTestRule
import it.droidcon.testingdaggerrxjava.rules.TestSchedulerRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class TwitterExerciseTest {

    @get:Rule
    val rule = TestSchedulerRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

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
        runBlocking {
            twitter.postTweet(1, 5)
            delay(10)
            twitter.postTweet(1, 6)
            delay(10)
            twitter.postTweet(1, 7)
            delay(10)

            val test = twitter.getNewsFeed(1).test()
            test.assertValue(listOf(7, 6, 5))

            twitter.postTweet(1, 8)

            test.assertValues(listOf(7, 6, 5), listOf(8, 7, 6, 5))
        }
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

        runBlocking {
            twitter.postTweet(1, 5)
            delay(10)
            twitter.postTweet(2, 6)

            test1.assertValues(emptyList(), listOf(5), listOf(6, 5))
            test2.assertValues(emptyList(), listOf(5), listOf(6, 5))
        }

        twitter.unfollow(1, 2)
        twitter.unfollow(2, 1)

        test1.assertValues(emptyList(), listOf(5), listOf(6, 5), listOf(5))
        test2.assertValues(emptyList(), listOf(5), listOf(6, 5), listOf(6))
    }

    @Test
    fun `should limit the news feed to 10 tweets`() {
        runBlocking {
            for (i in 0 until 11) {
                twitter.postTweet(1, i)
                delay(5)
            }

            val test = twitter.getNewsFeed(1).test()
            val firstList = (10 downTo 1).map { it }.toList()
            test.assertValue(firstList)

            twitter.postTweet(1, 11)
            delay(5)

            test.assertValueCount(2)
            test.assertValues(firstList, (11 downTo 2).map { it }.toList())
        }
    }

    @Test
    fun `should get all the followees of the followees`() {
        runBlocking {
            twitter.apply {
                follow(2, 1)
                delay(10)
                follow(3, 1)
                delay(10)
                follow(4, 1)
                delay(10)
                follow(3, 2)
                delay(10)
                follow(4, 2)
                delay(10)
                follow(5, 3)
                delay(10)
                follow(6, 5)
                delay(10)
                follow(7, 4)
                delay(10)
            }

            twitter.getFollowees(1).test().assertValue(setOf(2, 3, 4, 5, 7))
        }
    }
}