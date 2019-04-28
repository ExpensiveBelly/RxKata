package algorithm

fun main() {
    //3, 1, 2, 3, 4, 5
    val first = ListNode(1)
    val second = ListNode(2)
    val third = ListNode(3)
    val fourth = ListNode(2)
    val fifth = ListNode(1)
//    val sixth = ListNode(1)

    first.next = second
    second.next = third
    third.next = fourth
    fourth.next = fifth
//    fifth.next = sixth

//    println(removeKFromList(first, 3))
//    println(isListPalindrome(first))

//    val list_a = fromArray(intArrayOf(4, 7, 4, 2031))
//    val list_b = fromArray(intArrayOf(5, 3, 7, 4, 7, 9099))
//    println("Sum: " + addTwoHugeNumbers(list_a, list_b))
//    val (a, b) = Resources.fromJsonFile("test-13.json")
//    addTwoHugeNumbers(a, b)?.print()
//    addTwoHugeNumbers(a!!, b!!)?.print()
    println(mergeTwoLinkedLists(fromArray(intArrayOf(1, 2, 3)), fromArray(intArrayOf(4, 5, 6))))
}

/**
 * Given two singly linked lists sorted in non-decreasing order, your task is to merge them. In other words, return a
 * singly linked list, also sorted in non-decreasing order, that contains the elements from both original lists.
 */

fun mergeTwoLinkedLists(l1: ListNode<Int>?, l2: ListNode<Int>?): ListNode<Int>? {
    var head = ListNode(0)
    val l = head
    var list1 = l1
    var list2 = l2
    while (list1 != null || list2 != null) {
        var node: ListNode<Int>?
        if (compareValuesNullBigger(list1?.value, list2?.value) < 0) {
            node = ListNode(list1!!.value)
            list1 = list1.next
        } else {
            node = ListNode(list2!!.value)
            list2 = list2.next
        }
        head.next = node
        head = node
    }
    return l.next
}

/**
 * Compares two nullable [Comparable] values. Null is considered more than any value.
 *
 * @sample samples.comparisons.Comparisons.compareValues
 */

private fun <T : Comparable<*>> compareValuesNullBigger(a: T?, b: T?): Int {
    if (a === b) return 0
    if (a == null) return 1
    if (b == null) return -1

    @Suppress("UNCHECKED_CAST")
    return (a as Comparable<Any>).compareTo(b)
}

private fun fromArray(arr: IntArray): ListNode<Int>? {
    val res = ListNode(0)
    var current = res
    for (i in arr.indices) {
        val node = ListNode(arr[i])
        current.next = node
        current = node
    }
    return res.next
}

private fun IntArray.toListNode(): ListNode<Int>? {
    var listNode: ListNode<Int> = ListNode(0)
    val head = listNode
    forEach {
        val node = ListNode(it)
        listNode.next = node
        listNode = node
    }
    return head.next
}

/**
 * You're given 2 huge integers represented by linked lists.
 * Each linked list element is a number from 0 to 9999 that represents a number with exactly 4 digits.
 * The represented number might have leading zeros. Your task is to add up these huge integers and return the result in the same format.
 */


fun addTwoHugeNumbers(a: ListNode<Int>?, b: ListNode<Int>?): ListNode<Int>? {
    var headA: ListNode<Int>? = a
    var prev: ListNode<Int>? = null
    var headB: ListNode<Int>? = b
    var returnHead: ListNode<Int>? = null

    while (headA != null) {
        val next = headA.next
        headA.next = prev
        prev = headA
        headA = next
    }

    headA = prev
    prev = null

    while (headB != null) {
        val next = headB.next
        headB.next = prev
        prev = headB
        headB = next
    }
    headB = prev

    var carry = 0
    while (headA != null && headB != null) {
        var sum = headA.value + headB.value + carry
        headA = headA.next
        headB = headB.next

        carry = sum / 10000
        sum %= 10000

        val newNode = ListNode(sum)
        newNode.next = returnHead
        returnHead = newNode
    }

    while (headA != null) {
        var sum = headA.value + carry
        headA = headA.next

        carry = sum / 10000
        sum = sum % 10000
        val newNode = ListNode(sum)

        newNode.next = returnHead
        returnHead = newNode
    }

    while (headB != null) {

        var sum = headB.value + carry
        headB = headB.next

        carry = sum / 10000
        sum = sum % 10000

        val newNode = ListNode(sum)
        newNode.next = returnHead
        returnHead = newNode
    }
    if (carry != 0) {
        val newNode = ListNode(carry)
        newNode.next = returnHead
        returnHead = newNode
    }

    return returnHead
}

fun ListNode<Int>.print() {
    print("$value, ")
    next?.print()
}

/**
 * Given a singly linked list of integers, determine whether or not it's a palindrome.
 */

fun isListPalindrome(l: ListNode<Int>?): Boolean {
    if (l?.next == null) return true
    //Possible Solution: move pointer to the middle of the list, reverse the other half, and start comparing from there.
    //010, 0110, 0111110

    //How many nodes in the list?
    var tail = l
    var count = 1
    while (tail?.next != null) {
        tail = tail.next
        count++
    }

    val halfCount = count / 2

    //Reverse first half of the list
    var head = l
    var prev: ListNode<Int>? = null
    var current = head
    var next = head?.next
    repeat(halfCount) {
        current?.next = prev
        prev = current
        current = next
        next = next?.next
    }

    head = prev
    next = if (count % 2 == 0) current else next
    repeat(halfCount) {
        if (head?.value != next?.value) return false
        head = head?.next
        next = next?.next
    }

    return true
}

data class ListNode<T>(var value: T) {
    var next: ListNode<T>? = null

    override fun toString() = value?.let { "$it" + (if (next != null) ", $next" else "") } ?: ""
}

/**
 * Given a singly linked list of integers l and an integer k, remove all elements from list l that have a value equal to k.
 */


fun removeKFromList(l: ListNode<Int>?, k: Int): ListNode<Int>? {
    if (l == null) return null

    var head = l
    while (head?.value == k) {
        head = head.next
    }

    var node = head
    while (node?.next != null) {
        if (node.next?.value == k) {
            node.next = node.next?.next
        } else {
            node = node.next
        }
    }
    return head
}