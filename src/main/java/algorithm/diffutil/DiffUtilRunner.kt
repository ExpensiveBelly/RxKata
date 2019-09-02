package algorithm.diffutil

import algorithm.diffutil.callback.DefaultDiffUtilCallback
import algorithm.diffutil.callback.ExpandableListDiffCallback
import algorithm.diffutil.callback.listupdate.LoggingUpdateCallback

fun main() {
    flattenedLists(listOf("Hello"), listOf("Hello", "World"))

    nestedLists(listOf(Nested("Summary 1", listOf("Hello"))), listOf(Nested("Summary 1", listOf("Hello", "World"))))
}

data class Nested(val summary: String, val items: List<String>)

fun nestedLists(oldList: List<Nested>, newList: List<Nested>) {
    println("** Nested lists **")
    val oldListTransformed = oldList.flatMap { listOf(it.summary) + it.items }
    val newListTransformed = newList.flatMap { listOf(it.summary) + it.items }
    println("OldList : $oldListTransformed")
    println("NewList : $newListTransformed")
    calculateDiffUtil(ExpandableListDiffCallback(oldListTransformed, newListTransformed, oldList.map { it.items.size }, newList.map { it.items.size }), LoggingUpdateCallback())
}

private fun flattenedLists(oldList: List<String>, newList: List<String>) {
    println("** Flattened lists **")
    println("OldList : $oldList")
    println("NewList : $newList")
    calculateDiffUtil(DefaultDiffUtilCallback(oldList, newList, EqualsDiffCallback()), LoggingUpdateCallback())
}

