package kotlincollections

import kotlincollections.KotlinCollectionsSolutions.Person
import kotlincollections.KotlinCollectionsSolutions.Student
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class KotlinCollectionsSolutionsTest {

    private lateinit var exercises: KotlinCollectionsSolutions

    @Before
    fun setUp() {
        exercises = KotlinCollectionsSolutions()
    }

    @Test
    fun `accumulate names in a list`() {
        assertThat(exercises.`accumulate names in a list`()).isEqualTo(listOf("David", "Max", "Peter", "Pamela"))
    }

    @Test
    fun `find people of legal age, output formatted string`() {
        assertThat(exercises.`find people of legal age, output formatted string`()).isEqualTo("In Germany Max and Peter and Pamela are of legal age.")
    }

    @Test
    fun `group people by age, print age and names together`() {
        assertThat(exercises.`group people by age, print age and names together`()).isEqualTo(mapOf(18 to "Max", 23 to "Peter;Pamela", 12 to "David"))
    }

    @Test
    fun `lazily iterate Doubles, map to Int, map to String, print each`() {
        assertThat(exercises.`lazily iterate Doubles, map to Int, map to String, print each`()).isEqualTo("a1, a2, a3")
    }

    @Test
    fun `counting items in a list after filter is applied`() {
        assertThat(exercises.`counting items in a list after filter is applied`()).isEqualTo(2)
    }

    @Test
    fun `convert elements to strings and concatenate them, separated by commas`() {
        assertThat(exercises.`convert elements to strings and concatenate them, separated by commas`()).isEqualTo("laptop, monitor, mouse, keyboard")
    }

    @Test
    fun `compute sum of salaries of employee`() {
        assertThat(exercises.`compute sum of salaries of employee`()).isEqualTo(10_000)
    }

    @Test
    fun `compute sum of salaries by department`() {
        assertThat(exercises.`compute sum of salaries by department`()).isEqualTo(mapOf("IT" to 3_000, "Management" to 5_000, "HR" to 2_000))
    }

    @Test
    fun `partition students into passing and failing`() {
        assertThat(exercises.`partition students into passing and failing`()).isEqualTo(Pair(
                listOf(Student(10), Student(5)),
                listOf(Student(3), Student(4))))
    }

    @Test
    fun `names of male members`() {
        assertThat(exercises.`names of male members`()).isEqualTo(listOf("David", "Max", "Peter"))
    }

    @Test
    fun `group names of members in roster by gender`() {
        assertThat(exercises.`group names of members in roster by gender`()).isEqualTo(
                mapOf(Person.Sex.MALE to listOf("David", "Max", "Peter"),
                        Person.Sex.FEMALE to listOf("Pamela")))
    }

    @Test
    fun `filter a list to another list`() {
        assertThat(exercises.`filter a list to another list with items that start with o`()).isEqualTo(emptyList<String>())
    }

    @Test
    fun `finding shortest string a list`() {
        assertThat(exercises.`finding shortest string a list`()).isEqualTo("tray")
    }

    @Test
    fun `iterate an array, map the values, calculate the average`() {
        assertThat(exercises.`iterate an array, map the values, calculate the average`()).isEqualTo(5.0)
    }

    @Test
    fun `lazily iterate a list of strings, map the values, convert to Int, find max`() {
        assertThat(exercises.`lazily iterate a list of strings, map the values, convert to Int, find max`()).isEqualTo(3)
    }

    @Test
    fun `filter, upper case, then sort a list`() {
        assertThat(exercises.`filter, upper case, then sort a list`()).isEqualTo(listOf("C1", "C2"))
    }

    @Test
    fun `map names, join together with delimiter`() {
        assertThat(exercises.`map names, join together with delimiter`()).isEqualTo("DAVID | MAX | PETER | PAMELA")
    }

    @Test
    fun `reorder person list according to new ids`() {
        assertThat(exercises.`reorder person list according to person_ids`(listOf(4, 2, 3, 1))).isEqualTo(
                listOf(
                        Person(id = 4, name = "Pamela", age = 23, gender = Person.Sex.FEMALE),
                        Person(id = 2, name = "Max", age = 18, gender = Person.Sex.MALE),
                        Person(id = 3, name = "Peter", age = 23, gender = Person.Sex.MALE),
                        Person(id = 1, name = "David", age = 12, gender = Person.Sex.MALE)))

        assertThat(exercises.`reorder person list according to person_ids`(listOf(3, 4, 1, 2))).isEqualTo(
                listOf(
                        Person(id = 3, name = "Peter", age = 23, gender = Person.Sex.MALE),
                        Person(id = 4, name = "Pamela", age = 23, gender = Person.Sex.FEMALE),
                        Person(id = 1, name = "David", age = 12, gender = Person.Sex.MALE),
                        Person(id = 2, name = "Max", age = 18, gender = Person.Sex.MALE)))
    }
}
