package com.spicysoft.jwig.renderer;

import java.util.Collection;
import java.util.Map;

import com.spicysoft.jwig.Countable;
import com.spicysoft.jwig.Variables;
import com.spicysoft.lib.ArrayIterable;

public final class NormalLoop extends Loop
{
	private final String ident;

	@SuppressWarnings("unchecked")
	public NormalLoop(final Loop parent,final Object container,final String ident)
	{
		super(parent);
		if (container == null) {
			throw new IllegalArgumentException("Null Argument: container");
		}
		if (ident == null) {
			throw new IllegalArgumentException("Null Argument: ident");
		}

		final Iterable<Object> iterable;
		if (Object[].class.isAssignableFrom(container.getClass())) {
			final Object[] array = (Object[]) container;
			this.countable = true;
			this.length = array.length;
			iterable = new ArrayIterable<Object>(array);
		} else if (container instanceof Collection<?>) {
			final Collection<Object> collection = (Collection<Object>) container;
			this.countable = true;
			this.length = collection.size();
			iterable = collection;
		} else if (container instanceof Map<?, ?>) {
			final Map<Object, Object> map = (Map<Object, Object>) container;
			this.countable = true;
			this.length = map.size();
			iterable = map.values();
		} else if (container instanceof Iterable<?>) {
			if (container instanceof Countable) {
				this.countable = true;
				this.length    = ((Countable)container).size();
			} else {
				this.countable = false;
				this.length = -1;
			}
			iterable = (Iterable<Object>) container;
		} else {
			throw new IllegalArgumentException("Invalid Type Argument: " + container);
		}

		this.ident    = ident;
		this.iterator = iterable.iterator();
	}

	@Override public void onPerLoop(final Variables variables,final Object current)
	{
		variables.put(ident, current);
	}

	@Override public void onAfterLoop(final Variables values)
	{
		values.remove(ident);
	}

}
