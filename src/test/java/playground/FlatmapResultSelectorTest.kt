package playground

import org.junit.Rule
import org.junit.Test
import rules.TestSchedulerRule
import utils.Result

class FlatmapResultSelectorTest {

    @get:Rule
    val schedulerRule = TestSchedulerRule()

    private val flatmapResultSelector = FlatmapResultSelector()

    @Test
    fun should_return_result() {
        val name = "Will"
        flatmapResultSelector.`flatmap with result selector`()
                .test()
                .assertValue(Pair(name, Result.Success(name)))
    }
}