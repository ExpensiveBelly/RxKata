package algorithm.diffutil

fun main() {
    val oldList = mutableListOf("Hello", "World")
    val newList = mutableListOf("Hello")
    DiffUtilCalculator(oldList, newList, object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }).dispatchDiffUpdatesTo(object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            println("onInserted : position = [${position}], count = [${count}]")
        }

        override fun onRemoved(position: Int, count: Int) {
            println("onRemoved : position = [${position}], count = [${count}]")
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            println("onMoved : fromPosition = [${fromPosition}], toPosition = [${toPosition}]")
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            println("onChange: position = [${position}], count = [${count}], payload = [${payload}]")
        }
    })
}