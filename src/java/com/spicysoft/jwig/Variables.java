package com.spicysoft.jwig;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Variables
{
	private final static Log L = LogFactory.getLog(Variables.class);
	private final Map<String,Object> variables;

	public Variables()
	{
		this(new HashMap<String,Object>());
	}

	public Variables(final Map<String,Object> raw)
	{
		if (raw == null) {
			throw new IllegalArgumentException();
		}
		this.variables = raw;
	}

	public final void put(final String key,final Object value)
	{
		if (key == null) {
			throw new JwigRuntimeException("Null Argument: key");
		}
		if (value == null) {
			throw new JwigRuntimeException("Null Value: value");
		}
		if (L.isTraceEnabled()) {
			L.trace(String.format("PutVariable: key='%s', value='%s'",key,value));
		}
		this.variables.put(key, value);
	}

	public final Object get(final String key)
	{
		if (key == null) {
			throw new JwigRuntimeException("Null Argument: key");
		}
		final Object tmp = this.variables.get(key);
		if (L.isTraceEnabled()) {
			L.trace(String.format("GetVariable: key='%s', value='%s', variables=%s",key,tmp,variables));
		}
		assert(tmp != null);
		return tmp;
	}

	public final boolean contains(final String key,final Class<?> type)
	{
		if (key == null) {
			throw new JwigRuntimeException("Null Argument: key");
		}
		if (type == null) {
			throw new JwigRuntimeException("Null Argument: type");
		}
		if (!variables.containsKey(key)) {
			return false;
		}
		final Object tmp = variables.get(key);
		assert(tmp != null);
		return type.isAssignableFrom(tmp.getClass());
	}

	public final void remove(final String key)
	{
		if (key == null) {
			throw new JwigRuntimeException("Null Argument: key");
		}
		if (L.isTraceEnabled()) {
			if (variables.containsKey(key)) {
				final Object value = variables.get(key);
				L.trace(String.format("Removed : key=%s, value=%s",key,value));
			} else {
				L.trace(String.format("Not Removed Because No Entry: key=%s",key));
			}
		}
		variables.remove(key);
	}
}

