package marcinmoskala

import org.junit.Test

class KotlinAcademyTest {

    @Test
    fun group_by_is_the_opposite_of_flatmap() {
        val names = listOf("John", "Simon", "Andrew", "Peter")

        assert(names.groupBy { it.first() }.flatMap { it.value } == names)
    }
}