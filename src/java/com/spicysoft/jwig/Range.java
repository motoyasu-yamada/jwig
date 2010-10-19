package com.spicysoft.jwig;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class Range implements Iterable<Object>,Countable,Iterator<Object>
{
	private static final Log L = LogFactory.getLog(Range.class);

	public static Range newRange(final Object from,final Object to)
	{
		final Range range;
		if (from instanceof String && to instanceof String) {
			range = new StringRange(((String)from).charAt(0),((String)to).charAt(0));
		} else if (from instanceof Number && to instanceof Number) {
			range = new IntegerRange(((Number)from).intValue(),((Number)to).intValue());
		} else {
			throw new IllegalArgumentException("Range parameter should be Number or String");
		}
		if (L.isTraceEnabled()) {
			L.trace(range);
		}
		return range;
	}

	private int index = 0;

	@Override public Iterator<Object> iterator() {
		return this;
	}

	@Override public boolean hasNext() {
		return index < size();
	}

	@Override public Object next() {
		Object object = at(index++);
		return object;
	}

	@Override public void remove() {
		throw new UnsupportedOperationException();
	}

	protected abstract Object at(final int index);
	public abstract int size();

	static class StringRange extends Range
	{
		private final char from;
		private final char to;
		private final int direction;

		private StringRange(final char from,final char to)
		{
			this.from = from;
			this.to   = to;
			this.direction = from <= to ? 1 : -1;
		}

		protected final Object at(final int index)
		{
			return Character.toString((char)(from + index * this.direction));
		}

		public final int size()
		{
			return this.direction * (to - from) + 1;
		}

		public final String toString()
		{
			return "StringRange:{from:'" + from + "',to:'" + to + "',direction:'" + direction + "'}";
		}
	}

	static class IntegerRange extends Range
	{
		private final int from;
		private final int to;
		private final int direction;

		private IntegerRange(final int from, final int to)
		{
			this.from = from;
			this.to   = to;
			this.direction = this.from <= this.to ? 1 : -1;
		}

		protected final Object at(final int index)
		{
			return new Integer(from + index * this.direction);
		}

		public final int size()
		{
			return this.direction * (to - from) + 1;
		}

		public final String toString()
		{
			return "IntegerRange:{from:'" + from + "',to:'" + to + "',direction:'" + direction + "'}";
		}
	}
}
