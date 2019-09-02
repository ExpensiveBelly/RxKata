package algorithm.diffutil

import io.reactivex.annotations.NonNull


fun calculateDiffUtil(diffCallback: DiffUtil.Callback, listUpdateCallback: ListUpdateCallback) = dispatchDiffUpdatesTo(diffCallback, listUpdateCallback)

fun dispatchDiffUpdatesTo(diffUtilCallback: DiffUtil.Callback, @NonNull updateCallback: ListUpdateCallback) =
        DiffUtil.calculateDiff(diffUtilCallback).dispatchUpdatesTo(updateCallback)