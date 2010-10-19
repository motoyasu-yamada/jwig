package com.spicysoft.jwig.filter;

import com.spicysoft.jwig.Environment;
import com.spicysoft.jwig.Filter;
import com.spicysoft.jwig.renderer.Null;

public class NullFilter implements Filter
{
	public static final NullFilter INSTANCE = new NullFilter();

	private NullFilter()
	{
	}

	@Override public Object invoke(Environment environment, Object piped, Object[] args)
	{
		return Null.INSTANCE;
	}

}
