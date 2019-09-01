package algorithm.diffutil

import algorithm.diffutil.callback.DefaultDiffUtilCallback
import algorithm.diffutil.callback.ExpandableListDiffCallback

fun main() {
    flattenedLists(listOf("Hello"), listOf("Hello", "World"))

    nestedLists(listOf(Nested("Summary 1", listOf("Hello"))), listOf(Nested("Summary 2", listOf("Hello", "World"))))
}

data class Nested(val summary: String, val items: List<String>)

fun nestedLists(oldList: List<Nested>, newList: List<Nested>) {
    println("** Nested lists **")
    calculateDiffUtil(oldList, newList, ExpandableListDiffCallback(oldList, newList, oldList.map { it.items.size }, newList.map { it.items.size }))
}

private fun flattenedLists(oldList: List<String>, newList: List<String>) {
    println("** Flattened lists **")
    calculateDiffUtil(oldList, newList, DefaultDiffUtilCallback(oldList, newList, EqualsDiffCallback()))
}

