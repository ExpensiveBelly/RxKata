package playground.stream.flow

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import playground.stream.InfoType

@ExperimentalCoroutinesApi
@FlowPreview
class TweetsPostsFlowRepositoryTest {
	private val streamChannel = BroadcastChannel<TweetsPostsFlowExercise.InfoItemTO>(1)

	private val infoApi = mockk<TweetsPostsFlowExercise.InfoFlowApi> {
		coEvery { currentTweets() } returns mockk {
			every { items } returns INFO_TO_LIST
		}
		every { stream } returns streamChannel.asFlow()
	}
	private val repository = TweetsPostsFlowRepository(infoApi)

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun name() {
		runBlockingTest {
			streamChannel.send(TweetsPostsFlowExercise.InfoItemTO("id2", InfoType.TWEETS.name))
			assertEquals(TweetsPostsFlowExercise.InfoTO(INFO_TO_LIST),
				repository.getContentItemsObservable(InfoType.TWEETS).first())
		}
	}

	companion object {
		private val INFO_TO_LIST = listOf(TweetsPostsFlowExercise.InfoItemTO("id1", InfoType.TWEETS.name))
	}
}