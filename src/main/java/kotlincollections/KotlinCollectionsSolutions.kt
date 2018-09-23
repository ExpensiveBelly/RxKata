package kotlincollections

private const val PASS_THRESHOLD = 5

class KotlinCollectionsSolutions {

    data class Person(val id: Long, val name: String, val age: Int, val gender: Sex = Sex.MALE) {
        enum class Sex {
            MALE,
            FEMALE
        }
    }

    private val persons = listOf(
            Person(1, "David", 12),
            Person(2, "Max", 18),
            Person(3, "Peter", 23),
            Person(4, "Pamela", 23, Person.Sex.FEMALE))

    private val items = listOf("trampoline", "house", "computer", "tray")
    private val things = listOf("laptop", "monitor", "mouse", "keyboard")
    private val employees = listOf(
            Employee(1_200, "IT"),
            Employee(1_000, "IT"),
            Employee(800, "IT"),
            Employee(2_000, "HR"),
            Employee(5_000, "Management"))
    private val students = listOf(Student(10), Student(5), Student(3), Student(4))

    fun `accumulate names in a list`() = persons.map { it.name }

    fun `find people of legal age, output formatted string`() = persons
            .filter { it.age >= 18 }
            .map { it.name }
            .joinToString(" and ", "In Germany ", " are of legal age.")


    fun `group people by age, print age and names together`(): Map<Int, String> {
        val map1 = persons.map { it.age to it.name }.toMap()
        println(map1)
        // output: {18=Max, 23=Pamela, 12=David}
        // Result: duplicates overridden again

        val map4 = persons.groupBy { it.age }
        println(map4)
        // output: {18=[Person(name=Max, age=18)], 23=[Person(name=Peter, age=23), Person(name=Pamela, age=23)], 12=[Person(name=David, age=12)]}
        // Result: closer, but now have a Map<Int, List<Person>> instead of Map<Int, String>

        val map5 = persons.groupBy { it.age }.mapValues { it.value.map { it.name } }
        println(map5)
        // output: {18=[Max], 23=[Peter, Pamela], 12=[David]}
        // Result: closer, but now have a Map<Int, List<String>> instead of Map<Int, String>

        val map6 = persons.groupBy { it.age }.mapValues { it.value.joinToString(";") { it.name } }
        println(map6)
        // output: {18=Max, 23=Peter;Pamela, 12=David} // Result: YAY!!
        return map6
    }

    fun `lazily iterate Doubles, map to Int, map to String, print each`(): String =
            sequenceOf(1.0, 2.0, 3.0).map(Double::toInt).map { "a$it" }.joinToString()

    fun `counting items in a list after filter is applied`(): Int =
    // val count = items.filter { it.startsWith('t') }.size
    // but better to not filter, but count with a predicate
            items.count { it.startsWith('t') }

    fun `convert elements to strings and concatenate them, separated by commas`(): String = things.joinToString()

    data class Employee(val salary: Int, val department: String)

    fun `compute sum of salaries of employee`() = employees.sumBy { it.salary }

    fun `compute sum of salaries by department`() = employees.groupBy { it.department }.mapValues { it.value.sumBy { it.salary } }

    data class Student(val grade: Int)

    fun `partition students into passing and failing`() = students.partition { it.grade >= PASS_THRESHOLD }

    fun `names of male members`() = persons.filter { it.gender == Person.Sex.MALE }.map { it.name }

    fun `group names of members in roster by gender`(): Map<Person.Sex, List<String>> =
            persons.groupBy { it.gender }.mapValues { it.value.map { it.name } }

    fun `filter a list to another list with items that start with o`() = items.filter { it.startsWith('o') }

    fun `finding shortest string a list`() = items.minBy { it.length }


    fun `iterate an array, map the values, calculate the average`(): Double =
    // arrayOf(1, 2, 3).map { 2 * it + 1 }.average().apply(::println)
            arrayOf(1, 2, 3).map { 2 * it + 1 }.average()

    fun `lazily iterate a list of strings, map the values, convert to Int, find max`(): Int? =
            sequenceOf("a1", "a2", "a3")
                    .map { it.substring(1) }
                    .map(String::toInt)
                    .max()

    fun `lazily iterate a stream of Ints, map the values, print results`() {
        (1..3).map { "a$it" }.forEach(::println)
    }

    fun `filter, upper case, then sort a list`(): List<String> =
            listOf("a1", "a2", "b1", "c2", "c1").filter { it.startsWith('c') }.map(String::toUpperCase).sorted()

    // Kotlin:
    private inline fun String?.ifPresent(thenDo: (String) -> Unit) = this?.apply { thenDo(this) }

    fun `eager using first item if it exists`() {
        listOf("a1", "a2", "a3").firstOrNull()?.apply(::println)

        // now use the new extension function:
        listOf("a1", "a2", "a3").firstOrNull().ifPresent(::println)
    }

    fun `map names, join together with delimiter`(): String = persons.joinToString(" | ") { it.name.toUpperCase() }

    // Kotlin:
    inline fun Collection<Int>.summarizingInt(): SummaryStatisticsInt = this.fold(SummaryStatisticsInt()) { stats, num -> stats.accumulate(num) }

    data class SummaryStatisticsInt(var count: Int = 0, var sum: Int = 0,
                                    var min: Int = Int.MAX_VALUE, var max: Int = Int.MIN_VALUE, var avg: Double = 0.0) {
        fun accumulate(newInt: Int): SummaryStatisticsInt {
            count++
            sum += newInt
            min = min.coerceAtMost(newInt)
            max = max.coerceAtLeast(newInt)
            avg = sum.toDouble() / count
            return this
        }
    }

    inline fun <T : Any> Collection<T>.summarizingInt(transform: (T) -> Int): SummaryStatisticsInt = this.fold(SummaryStatisticsInt()) { stats, item -> stats.accumulate(transform(item)) }

    fun `collect with SummarizingInt`() {
        val stats2 = persons.map { it.age }.summarizingInt()

        val stats3 = persons.summarizingInt { it.age }
    }

    fun `reorder person list according to person_ids`(personIds: List<Long>): List<Person> {
        val originalListIndexes = (0..(persons.size - 1)).toList()
        val newListIndexesFromPersonId = personIds.map { personId ->
            persons.indexOfFirst { it.id == personId }
        }
        return newListIndexesFromPersonId.takeIf { it != originalListIndexes }?.let {
            persons.slice(it)
        } ?: emptyList()
    }
}
