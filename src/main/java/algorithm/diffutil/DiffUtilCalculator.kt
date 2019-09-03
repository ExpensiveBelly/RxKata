package algorithm.diffutil


fun calculateDiffUtilAndDispatch(diffCallback: DiffUtil.Callback, listUpdateCallback: ListUpdateCallback) = calculateDiffResult(diffCallback).dispatchUpdatesTo(listUpdateCallback)

fun calculateDiffResult(diffCallback: DiffUtil.Callback) = DiffUtil.calculateDiff(diffCallback)

