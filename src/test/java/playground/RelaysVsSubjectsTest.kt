package playground

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

class RelaysVsSubjectsTest {

    private val loadMoreSubject: PublishSubject<Unit> = PublishSubject.create()

    @Test
    fun subjects_do_not_retry_after_error() {
        val storyRepository = StoryRepository()
        val latch = CountDownLatch(1)
        val actuallyReceived = AtomicInteger()

        val observable = loadMoreSubject
                .flatMap {
                    storyRepository.fetchNextStoriesThrowsIOException()
                }.doOnEach {
                    actuallyReceived.incrementAndGet()
                    latch.countDown()
                }
                .test()

        clickLoadMore()

        latch.await()

        observable.assertError(IOException::class.java).also { clickLoadMore() }
        assertThat(actuallyReceived.get()).isEqualTo(1)
    }


    @Test
    fun subjects_retry_if_stream_not_in_a_terminal_state() {
        val storyRepository = StoryRepository()
        val latch = CountDownLatch(1)
        val actuallyReceived = AtomicInteger()

        val observable = loadMoreSubject
                .flatMap {
                    storyRepository.fetchNextStories()
                }.doOnEach {
                    actuallyReceived.incrementAndGet()
                    latch.countDown()
                }
                .test()

        clickLoadMore()

        latch.await()

        observable.assertNoErrors().also { clickLoadMore() }
        assertThat(actuallyReceived.get()).isEqualTo(2)
    }

    private fun clickLoadMore() = loadMoreSubject.onNext(Unit)

    class StoryRepository {

        fun fetchNextStoriesThrowsIOException(): Observable<Story> = Observable.fromCallable {
            throw IOException()
        }

        fun fetchNextStories(): Observable<Story> = Observable.fromCallable {
            return@fromCallable Story()
        }
    }
}

class Story
