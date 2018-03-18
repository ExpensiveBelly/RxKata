package kotlincollections

private const val PASS_THRESHOLD = 5

class KotlinCollectionsExercises {

    class Person(val name: String, val age: Int, val gender: Sex = Sex.MALE) {
        enum class Sex {
            MALE,
            FEMALE
        }
    }

    private val persons = listOf(
            Person("David", 12),
            Person("Max", 18),
            Person("Peter", 23),
            Person("Pamela", 23, Person.Sex.FEMALE))

    private val items = listOf("trampoline", "house", "computer", "tray")
    private val things = listOf("laptop", "monitor", "mouse", "keyboard")
    private val employees = listOf(
            Employee(1_200, "IT"),
            Employee(1_000, "IT"),
            Employee(800, "IT"),
            Employee(2_000, "HR"),
            Employee(5_000, "Management"))
    private val students = listOf(Student(10), Student(5), Student(3), Student(4))

    fun `accumulate names in a list`() {
        //persons
        throw NotImplementedError()
    }

    fun `find people of legal age, output formatted string`() {
        //persons
        throw NotImplementedError()
    }


    fun `group people by age, print age and names together`(): Map<Int, String> {
        //persons
        throw NotImplementedError()
    }

    fun `lazily iterate Doubles, map to Int, map to String, print each`(): String {
        //sequenceOf(1.0, 2.0, 3.0)
        throw NotImplementedError()
    }


    fun `counting items in a list after filter is applied only items that start with t`(): Int {
        //items
        throw NotImplementedError()
    }

    fun `convert elements to strings and concatenate them, separated by commas`(): String {
        //things
        throw NotImplementedError()
    }

    data class Employee(val salary: Int, val department: String)

    fun `compute sum of salaries of employee`() {
        // employees
        throw NotImplementedError()
    }

    fun `compute sum of salaries by department`() {
        //employees
        throw NotImplementedError()
    }

    data class Student(val grade: Int)

    fun `partition students into passing and failing`() {
        //students
        throw NotImplementedError()
    }

    fun `names of male members`() {
        //persons
        throw NotImplementedError()
    }

    fun `group names of members in roster by gender`() {
        //persons
        throw NotImplementedError()
    }

    fun `filter a list to another list with items that start with o`() {
        //items
        throw NotImplementedError()
    }

    fun `finding shortest string a list`() {
        //items
        throw NotImplementedError()
    }

    fun `iterate an array, map the values, calculate the average`() {
        //arrayOf(1, 2, 3)
        throw NotImplementedError()
    }

    fun `lazily iterate a list of strings, map the values, convert to Int, find max`() {
        //sequenceOf("a1", "a2", "a3")
        throw NotImplementedError()
    }

    fun `filter, upper case, then sort a list`() {
        //listOf("a1", "a2", "b1", "c2", "c1")
        throw NotImplementedError()
    }

    fun `map names, join together with delimiter`() {
        //persons
        throw NotImplementedError()
    }
}
