package com.spicysoft.jwig.filter;

import com.spicysoft.jwig.Environment;
import com.spicysoft.jwig.Filter;

public class FormatFilter implements Filter
{
	public static final String NAME = "format";

	@Override public Object invoke(Environment environment, Object piped, Object[] args)
	{
		final String format = (String)piped;
		return String.format(environment.getLocale(), format,args);
	}

}
