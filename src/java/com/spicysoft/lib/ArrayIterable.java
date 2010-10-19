package com.spicysoft.lib;

import java.util.Iterator;

public final class ArrayIterable<T> implements Iterable<T>
{
	private final T[] array;

	public ArrayIterable(final T[] array)
	{
		if (array == null) {
			throw new IllegalArgumentException("Null Argument: array");
		}
		this.array = array;
	}

	@Override public Iterator<T> iterator()
	{
		return new ArrayIterator();
	}

	private final class ArrayIterator implements Iterator<T>
	{
		private int index = 0;

		@Override public boolean hasNext()
		{
			return index < array.length;
		}

		@Override public T next()
		{
			return array[index++];
		}

		@Override public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

}
