package playground

import com.nhaarman.mockito_kotlin.clearInvocations
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockitokotlin2.never
import org.junit.Test

class DisposeUpstreamErrorDownstreamTest {

    private val streamErrorMockForTesting = mock<StreamErrorMockForTesting> {}
    private val streamCompleteMockForTesting = mock<StreamCompleteMockForTesting> {}
    private val disposeUpstreamErrorDownstream =
        DisposeUpstreamErrorDownstream(streamErrorMockForTesting, streamCompleteMockForTesting)

    @Test
    fun `should dispose upstream and error downstream`() {
        val test = disposeUpstreamErrorDownstream.streamErrors.test()

        inOrder(streamErrorMockForTesting) {
            verify(streamErrorMockForTesting).beforeMapOnNext()
            verify(streamErrorMockForTesting).beforeMapOnDispose()
            verify(streamErrorMockForTesting, never()).beforeMapOnError()

            verify(streamErrorMockForTesting, never()).afterMapOnNext()
            verify(streamErrorMockForTesting).afterMapOnError()
            verify(streamErrorMockForTesting, never()).afterMapOnDispose()
        }
        verify(streamErrorMockForTesting).beforeMapOnDispose()
    }

    @Test
    fun `should propagate completion downstream and when the flatmap completes then the stream completes`() {
        disposeUpstreamErrorDownstream.streamCompletes.test()

        verify(streamCompleteMockForTesting).beforeFlatMap()
        verify(streamCompleteMockForTesting, never()).afterFlatMap()
        clearInvocations(streamCompleteMockForTesting)

        disposeUpstreamErrorDownstream.subject.onComplete()

        verify(streamCompleteMockForTesting, never()).beforeFlatMap()
        verify(streamCompleteMockForTesting).afterFlatMap()
    }
}