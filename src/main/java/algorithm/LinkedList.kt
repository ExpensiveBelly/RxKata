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
    println(isListPalindrome(first))
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