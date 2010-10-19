package com.spicysoft.jwig.renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MixedMap implements Iterable<Object>,Map<Object,Object>
{
	private final List<Object> list = new ArrayList<Object>();
	private final Map<Object,Object> map = new HashMap<Object,Object>();

	void internalAdd(final Object key,final Object value)
	{
		list.add(value);
		map.put(key, value);
	}

	@Override public boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}

	@Override public boolean containsValue(Object value)
	{
		return map.containsValue(value);
	}

	@Override public Set<Entry<Object, Object>> entrySet()
	{
		return map.entrySet();
	}

	@Override public Object get(Object key)
	{
		return map.get(key);
	}

	@Override public boolean isEmpty()
	{
		assert(list.isEmpty());
		return map.isEmpty();
	}

	@Override public Set<Object> keySet()
	{
		return map.keySet();
	}

	@Override public int size()
	{
		assert(list.size() == map.size());
		return map.size();
	}

	@Override public Collection<Object> values()
	{
		return map.values();
	}

	@Override public Iterator<Object> iterator()
	{
		return this.list.iterator();
	}

	@Deprecated
	@Override public void clear()
	{
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override public Object put(Object key, Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override public void putAll(Map<? extends Object, ? extends Object> m)
	{
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override public Object remove(Object key)
	{
		throw new UnsupportedOperationException();
	}

}
