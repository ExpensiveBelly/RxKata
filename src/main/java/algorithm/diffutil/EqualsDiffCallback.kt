package algorithm.diffutil

class EqualsDiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(item1: T, item2: T) = areContentsTheSame(item1, item2)

    override fun areContentsTheSame(item1: T, item2: T) = item1 == item2
}