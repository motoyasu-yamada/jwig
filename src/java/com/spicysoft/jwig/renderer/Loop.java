package com.spicysoft.jwig.renderer;

import java.util.Iterator;

import com.spicysoft.jwig.Variables;

abstract class Loop implements Iterable<Object>,Iterator<Object> {
	protected boolean countable;
	protected int length;
	protected Loop parent;
	protected Iterator<Object> iterator;
	protected int index0;

	protected Loop(final Loop parent) {
		this.index0 = -1;
		this.parent = parent;
	}

	public final int index() {
		return this.index0 + 1;
	}

	public final int index0() {
		return this.index0;
	}

	public final boolean first() {
		return this.index0 == 0;
	}

	public final Loop parent() {
		return this.parent;
	}

	public final int length() {
		verifyIsCountable();
		return this.length;
	}

	public final int revindex() {
		verifyIsCountable();
		return this.length - index() + 1;
	}

	public final int revindex0() {
		verifyIsCountable();
		return this.length - index();
	}

	public final boolean last() {
		verifyIsCountable();
		return revindex0() == 0;
	}

	@Override public Iterator<Object> iterator() {
		return this;
	}

	@Override public boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override public Object next() {
		this.index0++;
		return this.iterator.next();
	}

	/** You can call this method after you'll exit a loop. */
	public boolean isEmpty() {
		if (this.countable) {
			assert(length == 0 && index0 == 0 ||
				   length != 0 && index0 != 0);
			return length == 0;
		} else {
			return index0 == 0;
		}
	}

	@Override public void remove() {
		throw new UnsupportedOperationException();
	}

	protected abstract void onPerLoop(final Variables values,final Object current);

	protected abstract void onAfterLoop(final Variables values);

	private final void verifyIsCountable() {
		if (countable) {
			return;
		}
		throw new IllegalArgumentException("Not countable");
	}

}
