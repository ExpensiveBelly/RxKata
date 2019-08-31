package algorithm.diffutil;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

import java.util.List;

public class DiffUtilCalculator<T> {

	private final List<T> oldList;
	private final List<T> newList;
	private DiffUtil.ItemCallback<T> diffcallback;

	public DiffUtilCalculator(List<T> oldList, List<T> newList, DiffUtil.ItemCallback<T> diffcallback) {
		this.oldList = oldList;
		this.newList = newList;
		this.diffcallback = diffcallback;
	}

	public void dispatchDiffUpdatesTo(@NonNull ListUpdateCallback updateCallback) {
		DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
			@Override
			public int getOldListSize() {
				return oldList.size();
			}

			@Override
			public int getNewListSize() {
				return newList.size();
			}

			@Override
			public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
				T oldItem = oldList.get(oldItemPosition);
				T newItem = newList.get(newItemPosition);
				if (oldItem != null && newItem != null) {
					return diffcallback.areItemsTheSame(oldItem, newItem);
				}
				// If both items are null we consider them the same.
				return oldItem == null && newItem == null;
			}

			@Override
			public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
				T oldItem = oldList.get(oldItemPosition);
				T newItem = newList.get(newItemPosition);
				if (oldItem != null && newItem != null) {
					return diffcallback.areContentsTheSame(oldItem, newItem);
				}
				if (oldItem == null && newItem == null) {
					return true;
				}
				// There is an implementation bug if we reach this point. Per the docs, this
				// method should only be invoked when areItemsTheSame returns true. That
				// only occurs when both items are non-null or both are null and both of
				// those cases are handled above.
				throw new AssertionError();
			}

			@Nullable
			@Override
			public Object getChangePayload(int oldItemPosition, int newItemPosition) {
				T oldItem = oldList.get(oldItemPosition);
				T newItem = newList.get(newItemPosition);
				if (oldItem != null && newItem != null) {
					return diffcallback.getChangePayload(oldItem, newItem);
				}
				// There is an implementation bug if we reach this point. Per the docs, this
				// method should only be invoked when areItemsTheSame returns true AND
				// areContentsTheSame returns false. That only occurs when both items are
				// non-null which is the only case handled above.
				throw new AssertionError();
			}
		});
		diffResult.dispatchUpdatesTo(updateCallback);
	}
}
