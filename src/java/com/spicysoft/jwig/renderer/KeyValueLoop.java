package com.spicysoft.jwig.renderer;

import java.util.Map;
import java.util.Set;

import com.spicysoft.jwig.Variables;


public final class KeyValueLoop extends Loop
{
	private final String keyIdent;
	private final String valueIdent;

	@SuppressWarnings("unchecked")
	public KeyValueLoop(final Loop parent, final Object container,
			final String keyIdent, final String valueIdent) {
		super(parent);
		if (container == null) {
			throw new IllegalArgumentException(
					"Null Argument: container");
		}
		if (keyIdent == null) {
			throw new IllegalArgumentException(
					"Null Argument: keyIdent");
		}
		if (valueIdent == null) {
			throw new IllegalArgumentException(
					"Null Argument: valueIdent");
		}
		if (keyIdent.equals(valueIdent)) {
			throw new IllegalArgumentException("'valueIdent'(" + valueIdent
					+ ") shoud be defferent from 'keyIdent'(" + keyIdent
					+ "), but is same.");
		}
		final Map<Object, Object> map;
		if (container instanceof Map<?, ?>) {
			map = (Map<Object, Object>) container;
		} else {
			throw new IllegalArgumentException(
					"a variable MAP in 'for KEY,VALUE in MAP' statement should be instance of java.lang.Map");
		}
		this.keyIdent = keyIdent;
		this.valueIdent = valueIdent;
		this.countable = true;
		this.length = map.size();
		this.iterator = ((Set<Object>) (Object) map.entrySet()).iterator();
	}

	@SuppressWarnings("unchecked")
	@Override public void onPerLoop(final Variables variables, final Object current)
	{
		assert(variables != null);
		assert(current != null);
		assert(current instanceof Map.Entry<?, ?>);

		final Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>)current;
		variables.put(keyIdent,  entry.getKey());
		variables.put(valueIdent,entry.getValue());
	}

	@Override public void onAfterLoop(final Variables variables)
	{
		variables.remove(keyIdent);
		variables.remove(valueIdent);
	}
}
