package algorithm

fun main() {
    //3, 1, 2, 3, 4, 5
    val first = ListNode(3)
    val second = ListNode(1)
    val third = ListNode(2)
    val fourth = ListNode(3)
    val fifth = ListNode(4)
    val sixth = ListNode(5)

    first.next = second
    second.next = third
    third.next = fourth
    fourth.next = fifth
    fifth.next = sixth

    println(removeKFromList(first, 3))
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