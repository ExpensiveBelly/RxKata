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