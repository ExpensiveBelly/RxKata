package algorithm


data class LinkedListInput(val input: Input)

data class Input(val a: IntArray, val b: IntArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Input

        if (!a.contentEquals(other.a)) return false
        if (!b.contentEquals(other.b)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = a.contentHashCode()
        result = 31 * result + b.contentHashCode()
        return result
    }
}