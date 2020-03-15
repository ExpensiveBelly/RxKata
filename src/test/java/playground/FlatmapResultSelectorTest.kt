package playground

import it.droidcon.testingdaggerrxjava.TrampolineSchedulerRule
import org.junit.Rule
import org.junit.Test
import utils.Result

class FlatmapResultSelectorTest {

    @get:Rule
    val schedulerRule = TrampolineSchedulerRule()

    private val flatmapResultSelector = FlatmapResultSelector()

    @Test
    fun should_return_result() {
        val name = "Will"
        flatmapResultSelector.`flatmap with result selector`()
                .test()
                .assertValue(Pair(name, Result.Success(name)))
    }
}