package playground.stream.flow

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import it.droidcon.testingdaggerrxjava.rules.CoroutineTestRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import playground.stream.InfoItem
import playground.stream.InfoType
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@FlowPreview
class TweetsPostsFlowRepositoryTest {

	@get:Rule
	val testCoroutineRule = CoroutineTestRule()

	private val streamChannel = ConflatedBroadcastChannel<TweetsPostsFlowExercise.InfoItemTO>()

	private val infoApi = mockk<TweetsPostsFlowExercise.InfoFlowApi> {
		coEvery { currentTweets() } returns TweetsPostsFlowExercise.InfoTO(INFO_TO_LIST)
		coEvery { getDetails(ID_1) } returns TweetsPostsFlowExercise.InfoItemDetailsTO(TITLE1, DATE)
		coEvery { getDetails(ID_2) } returns TweetsPostsFlowExercise.InfoItemDetailsTO(TITLE2, DATE)
		every { stream } returns streamChannel.asFlow()
	}
	private val repository = TweetsPostsFlowRepository(infoApi)

	@Test
	fun `start with empty list and then emit items`() {
		runBlockingTest {
			streamChannel.send(TweetsPostsFlowExercise.InfoItemTO(ID_2, InfoType.TWEETS.name))
			val contentItemsObservable = repository.getContentItemsObservable(InfoType.TWEETS)
			val items = contentItemsObservable.take(3).toList()
			assertEquals(emptyList<TweetsPostsFlowExercise.InfoItemTO>(), items[0])
			assertEquals(listOf(InfoItem(ID_1, TITLE1, DATE)), items[1])
			assertEquals(listOf(InfoItem(ID_1, TITLE1, DATE), InfoItem(ID_2, TITLE2, DATE)), items[2])
		}
	}

	companion object {
		private const val ID_1 = "id1"
		private const val ID_2 = "id2"
		private const val TITLE1 = "title1"
		private const val TITLE2 = "title2"
		private const val DATE = "1/12/98"
		private val INFO_TO_LIST = listOf(TweetsPostsFlowExercise.InfoItemTO(ID_1, InfoType.TWEETS.name))
	}
}