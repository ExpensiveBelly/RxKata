package novoda.types;

import java.util.Iterator;

public class IntegerOperator implements Iterator<Integer> {

	private Integer counter = 0;

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Integer next() {
		return counter++;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}

