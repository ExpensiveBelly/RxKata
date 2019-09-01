package algorithm.diffutil.callback


class ExpandableListDiffCallback<T>(
        oldItems: List<T>,
        newItems: List<T>,
        oldRowsPerItem: List<Int>,
        newRowsPerItem: List<Int>
) : GroupieDiffCallback<T>(oldItems, newItems, oldRowsPerItem, newRowsPerItem) {

    override fun areItemsTheSame(oldItem: T, oldChildIndex: Int, newItem: T, newChildIndex: Int) =
            if (oldChildIndex == 0) newChildIndex == 0 && arePrimaryItemsTheSame(oldItem, newItem)
            else newChildIndex != 0 && areExpansionItemsTheSame(oldItem, oldChildIndex - 1, newItem, newChildIndex - 1)

    override fun areContentsTheSame(oldItem: T, oldChildIndex: Int, newItem: T, newChildIndex: Int) =
            if (oldChildIndex == 0) newChildIndex == 0 && arePrimaryContentsTheSame(oldItem, newItem)
            else newChildIndex != 0 && areExpansionContentsTheSame(oldItem, oldChildIndex - 1, newItem, newChildIndex - 1)

    private fun arePrimaryItemsTheSame(oldItem: T, newItem: T) =
            super.areItemsTheSame(oldItem, 0, newItem, 0)

    private fun areExpansionItemsTheSame(oldItem: T, oldExpansionIndex: Int, newItem: T, newExpansionIndex: Int) =
            super.areItemsTheSame(oldItem, oldExpansionIndex + 1, newItem, newExpansionIndex + 1)

    private fun arePrimaryContentsTheSame(oldItem: T, newItem: T) =
            super.areContentsTheSame(oldItem, 0, newItem, 0)

    private fun areExpansionContentsTheSame(oldItem: T, oldExpansionIndex: Int, newItem: T, newExpansionIndex: Int) =
            super.areContentsTheSame(oldItem, oldExpansionIndex + 1, newItem, newExpansionIndex + 1)
}