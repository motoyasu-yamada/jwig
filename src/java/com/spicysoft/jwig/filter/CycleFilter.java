package com.spicysoft.jwig.filter;

import java.util.List;

import com.spicysoft.jwig.Environment;
import com.spicysoft.jwig.Filter;

public class CycleFilter implements Filter
{
	public static final String NAME = "cycle";

	@SuppressWarnings("unchecked")
	@Override public Object invoke(Environment environment, Object piped, Object[] params)
	{
		final List<Object> list = (List<Object>)piped;
		final int cycle = ((Number)params[0]).intValue();
		return list.get(cycle % list.size());
	}

}
