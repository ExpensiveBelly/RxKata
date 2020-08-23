package playground

import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Test

class DisposeUpstreamErrorDownstreamTest {

    private val streamErrorMockForTesting = mockk<StreamErrorMockForTesting>(relaxed = true) {}
    private val streamCompleteMockForTesting = mockk<StreamCompleteMockForTesting> {}
    private val disposeUpstreamErrorDownstream =
        DisposeUpstreamErrorDownstream(streamErrorMockForTesting, streamCompleteMockForTesting)

    @Test
    fun `should dispose upstream and error downstream`() {
        val test = disposeUpstreamErrorDownstream.streamErrors.test()

        verifyOrder {
            streamErrorMockForTesting.beforeMapOnNext()
            streamErrorMockForTesting.beforeMapOnDispose()
            streamErrorMockForTesting.afterMapOnError()
        }
        verify(exactly = 0) {
            streamErrorMockForTesting.afterMapOnNext()
            streamErrorMockForTesting.afterMapOnDispose()
        }
    }

    @Test
    fun `should propagate completion downstream and when the flatmap completes then the stream completes`() {
        disposeUpstreamErrorDownstream.streamCompletes.test()

        verify { streamCompleteMockForTesting.beforeFlatMap() }
        verify(exactly = 0) { streamCompleteMockForTesting.afterFlatMap() }
        clearMocks(streamCompleteMockForTesting)

        disposeUpstreamErrorDownstream.subject.onComplete()

        verify(exactly = 0) {
            streamCompleteMockForTesting.beforeFlatMap()
            streamCompleteMockForTesting.afterFlatMap()
        }
    }
}