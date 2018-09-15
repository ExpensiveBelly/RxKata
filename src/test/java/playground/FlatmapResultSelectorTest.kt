package playground

import it.droidcon.testingdaggerrxjava.TrampolineSchedulerRule
import org.junit.Rule
import org.junit.Test

class FlatmapResultSelectorTest {

    @get:Rule
    val schedulerRule = TrampolineSchedulerRule()

    private val flatmapResultSelector = FlatmapResultSelector()

    @Test
    fun should_return_result() {
        flatmapResultSelector.`flatmap with result selector`()
                .test()
                .assertValue(Pair("Will", Result.Success("Expensive")))
    }
}