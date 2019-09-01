package algorithm.diffutil.callback;

import algorithm.diffutil.DiffUtil;
import io.reactivex.annotations.Nullable;

import java.util.List;

/**
 * This version of the DiffUtil has been copy/pasted from androidx.recyclerview:recyclerview:1.0.0
 *
 * @param <T>
 */

public class DefaultDiffUtilCallback<T> extends DiffUtil.Callback {

	private final List<T> oldList;
	private final List<T> newList;
	private DiffUtil.ItemCallback<T> diffItemCallback;

	public DefaultDiffUtilCallback(List<T> oldList, List<T> newList, DiffUtil.ItemCallback<T> diffItemCallback) {
		this.oldList = oldList;
		this.newList = newList;
		this.diffItemCallback = diffItemCallback;
	}

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
			return diffItemCallback.areItemsTheSame(oldItem, newItem);
		}
		// If both items are null we consider them the same.
		return oldItem == null && newItem == null;
	}

	@Override
	public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
		T oldItem = oldList.get(oldItemPosition);
		T newItem = newList.get(newItemPosition);
		if (oldItem != null && newItem != null) {
			return diffItemCallback.areContentsTheSame(oldItem, newItem);
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
			return diffItemCallback.getChangePayload(oldItem, newItem);
		}
		// There is an implementation bug if we reach this point. Per the docs, this
		// method should only be invoked when areItemsTheSame returns true AND
		// areContentsTheSame returns false. That only occurs when both items are
		// non-null which is the only case handled above.
		throw new AssertionError();
	}
}
