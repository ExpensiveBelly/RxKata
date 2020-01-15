package playground

import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockitokotlin2.never
import org.junit.Test

class DisposeUpstreamErrorDownstreamTest {

    private val mockForTesting = mock<MockForTesting> {}
    private val disposeUpstreamErrorDownstream = DisposeUpstreamErrorDownstream(mockForTesting)

    @Test
    fun `should dispose upstream and error downstream`() {
        val test = disposeUpstreamErrorDownstream.stream.test()

        inOrder(mockForTesting) {
            verify(mockForTesting).beforeMapOnNext()
            verify(mockForTesting).beforeMapOnDispose()
            verify(mockForTesting, never()).beforeMapOnError()

            verify(mockForTesting, never()).afterMapOnNext()
            verify(mockForTesting).afterMapOnError()
            verify(mockForTesting, never()).afterMapOnDispose()
        }
        verify(mockForTesting).beforeMapOnDispose()
    }
}