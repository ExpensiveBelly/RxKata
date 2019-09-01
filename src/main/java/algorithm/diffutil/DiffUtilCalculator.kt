package algorithm.diffutil

import io.reactivex.annotations.NonNull


fun <T> calculateDiffUtil(oldList: List<T>, newList: List<T>, diffCallback: DiffUtil.Callback) {
    println("OldList : $oldList")
    println("NewList : $newList")
    dispatchDiffUpdatesTo(diffCallback, object : ListUpdateCallback {
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

fun dispatchDiffUpdatesTo(diffUtilCallback: DiffUtil.Callback, @NonNull updateCallback: ListUpdateCallback) =
        DiffUtil.calculateDiff(diffUtilCallback).dispatchUpdatesTo(updateCallback)