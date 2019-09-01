/*
 MIT License

Copyright (c) 2018 Fabio Barata

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package algorithm.diffutil.callback

import algorithm.diffutil.DiffUtil

abstract class GroupieDiffCallback<T>(
        private val oldItems: List<T>,
        private val newItems: List<T>,
        oldRowsPerItem: List<Int>,
        newRowsPerItem: List<Int>
) : DiffUtil.Callback() {
    private val oldPositionToItemIndex = convertToPositionToItemIndex(oldRowsPerItem)
    private val oldPositionToChildIndex = convertToPositionToChildIndex(oldRowsPerItem)

    private val newPositionToItemIndex = convertToPositionToItemIndex(newRowsPerItem)
    private val newPositionToChildIndex = convertToPositionToChildIndex(newRowsPerItem)

    private val oldListSize = oldRowsPerItem.sum()
    private val newListSize = newRowsPerItem.sum()

    override fun getOldListSize() = oldListSize
    override fun getNewListSize() = newListSize

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldChildIndex = oldPositionToChildIndex[oldItemPosition]
        val newChildIndex = newPositionToChildIndex[newItemPosition]

        val oldItemIndex = oldPositionToItemIndex[oldItemPosition]
        val newItemIndex = newPositionToItemIndex[newItemPosition]

        return areItemsTheSame(oldItems[oldItemIndex], oldChildIndex, newItems[newItemIndex], newChildIndex)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldChildIndex = oldPositionToChildIndex[oldItemPosition]
        val newChildIndex = newPositionToChildIndex[newItemPosition]

        val oldItemIndex = oldPositionToItemIndex[oldItemPosition]
        val newItemIndex = newPositionToItemIndex[newItemPosition]

        return areContentsTheSame(oldItems[oldItemIndex], oldChildIndex, newItems[newItemIndex], newChildIndex)
    }

    private fun convertToPositionToChildIndex(rowsPerItem: List<Int>) =
            rowsPerItem.foldIndexed(emptyList<Int>()) { itemIndex, itemHeaderIndexes, itemRows ->
                itemHeaderIndexes + (0 until itemRows)
            }

    private fun convertToPositionToItemIndex(rowsPerItem: List<Int>) =
            rowsPerItem.foldIndexed(emptyList<Int>()) { itemIndex, itemHeaderIndexes, itemRows ->
                itemHeaderIndexes + (0 until itemRows).map { itemIndex }
            }

    protected open fun areItemsTheSame(oldItem: T, oldChildIndex: Int, newItem: T, newChildIndex: Int) =
            areContentsTheSame(oldItem, oldChildIndex, newItem, newChildIndex)

    protected open fun areContentsTheSame(oldItem: T, oldChildIndex: Int, newItem: T, newChildIndex: Int) =
            oldItem == newItem && oldChildIndex == newChildIndex

}