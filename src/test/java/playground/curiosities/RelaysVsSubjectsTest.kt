package playground.curiosities

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.amshove.kluent.shouldEqual
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RelaysVsSubjectsTest {

    private val loadMoreSubject: PublishSubject<Unit> = PublishSubject.create()
    private val loadMoreRelay: PublishRelay<Unit> = PublishRelay.create()

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

        loadMoreSubject.onNext(Unit)

        latch.await()

        observable.assertError(IOException::class.java).also { loadMoreSubject.onNext(Unit) }
        assertThat(actuallyReceived.get()).isEqualTo(1)
    }

    /**
     * “Relays do not help us if we get an error while consuming the events from it and mapping into something else
     * i.e. downstream. In fact, a Relay would also let the stream crash just like a Subject in this case.
     * On the other hand, Relays are beneficial because they do not accept any terminal event from the producer,
     * i.e from the top.”
     */

    @Test
    fun relays_do_not_retry_after_error() {
        val storyRepository = StoryRepository()
        val latch = CountDownLatch(1)
        val actuallyReceived = AtomicInteger()

        val observable = loadMoreRelay
                .flatMap {
                    storyRepository.fetchNextStoriesThrowsIOException()
                }.doOnEach {
                    actuallyReceived.incrementAndGet()
                    latch.countDown()
                }
                .test()

        loadMoreRelay.accept(Unit)

        latch.await()

        observable.assertError(IOException::class.java).also { loadMoreRelay.accept(Unit) }
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

        loadMoreSubject.onNext(Unit)

        latch.await()

        observable.assertNoErrors().also { loadMoreSubject.onNext(Unit) }
        assertThat(actuallyReceived.get()).isEqualTo(2)
    }

    @Test
    fun subjects_consume_complete_or_error() {
        val result: MutableList<Int> = mutableListOf()
        val completingObservable = Observable.just(1)
                .doOnNext { result.add(it) }

        val subject = PublishSubject.create<Int>()
        completingObservable.subscribe(subject)

        //subject has received the onComplete() event from completingObservable and it has terminated
        assertTrue(subject.test().isTerminated)

        //Subsequent emissions are ignored
        val completingObservable2 = Observable.just(2)
                .doOnNext { result.add(it) }
        completingObservable2.subscribe(subject)

        result shouldEqual listOf(1)
    }

    @Test
    fun relays_do_not_consume_complete_or_error() {
        val result: MutableList<Int> = mutableListOf()
        val completingObservable = Observable.just(1)
                .doOnNext { result.add(it) }

        val relay = PublishRelay.create<Int>()
        completingObservable.subscribe(relay)

        //Relay does not consume the onComplete() event from completingObservable
        assertFalse(relay.test().isTerminated)

        val completingObservable2 = Observable.just(2)
                .doOnNext { result.add(it) }
        completingObservable2.subscribe(relay)

        result shouldEqual listOf(1, 2)
    }

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
