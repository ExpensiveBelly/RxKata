package it.droidcon.testingdaggerrxjava.playground.stream

import com.news.*
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import it.droidcon.testingdaggerrxjava.TrampolineSchedulerRule
import org.junit.Rule
import org.junit.Test
import playground.stream.*
import java.time.Instant
import java.util.*

class TweetsPostsRepositoryTest {

    @get:Rule
    val schedulerRule = TrampolineSchedulerRule()

    val tweetsSubject = SingleSubject.create<InfoTO>()
    val facebookPostsSubject = SingleSubject.create<InfoTO>()
    val streamSubject = PublishSubject.create<InfoItemTO>()
    val api = mock<InfoApi> {
        on { currentTweets } doReturn tweetsSubject
        on { currentFacebookPosts } doReturn facebookPostsSubject
        on { stream } doReturn streamSubject
    }
    val tweetsPostsRepository = TweetsPostsRepository(
            infoApi = api,
            ioScheduler = Schedulers.trampoline(),
            computationScheduler = Schedulers.trampoline())

    @Test
    fun `Given I subscribe to the stream Then items are emitted`() {
        val testObserver = tweetsPostsRepository.tweetsAndPostsStream.test()

        streamSubject.onNext(INFO_ITEM_TO_DELTA)

        testObserver.assertValue(INFO_ITEM_TO_DELTA)
    }

    @Test
    fun `Given I subscribe to aggregated tweets Then the initial value is emitted plus the delta emitted in the stream`() {
        val testObserver = tweetsPostsRepository.aggregatedTweetsObservable.test()

        val detailsSubject = SingleSubject.create<InfoItemDetailsTO>()
        val detailsSubject2 = SingleSubject.create<InfoItemDetailsTO>()
        whenever(api.getDetails(1.toString())).thenReturn(detailsSubject)
        whenever(api.getDetails(2.toString())).thenReturn(detailsSubject2)

        //Initial data
        tweetsSubject.onSuccess(InfoTO(listOf(INFO_ITEM_TO)))
        detailsSubject.onSuccess(INFO_ITEM_DETAILS_TO)

        //Delta
        streamSubject.onNext(INFO_ITEM_TO_DELTA)
        detailsSubject2.onSuccess(INFO_ITEM_DETAILS_TO)

        testObserver.assertValues(
                listOf(InfoItem(1.toString(), TITLE, DATE)),
                listOf(InfoItem(1.toString(), TITLE, DATE), InfoItem(2.toString(), TITLE, DATE)))
    }

    companion object {
        val INFO_ITEM_TO = InfoItemTO(id = 1.toString(), type = InfoType.TWEETS.name)
        val INFO_ITEM_TO_DELTA = InfoItemTO(id = 2.toString(), type = InfoType.TWEETS.name)

        val DATE = Date.from(Instant.now()).toString()
        val TITLE = "My title"
        val INFO_ITEM_DETAILS_TO = InfoItemDetailsTO(TITLE, DATE)
    }
}