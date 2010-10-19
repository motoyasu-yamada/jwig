package com.spicysoft.jwig.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spicysoft.jwig.Environment;
import com.spicysoft.jwig.Filter;
import com.spicysoft.jwig.renderer.Null;

public class DefaultFilter implements Filter
{
	private static final Log L = LogFactory.getLog(DefaultFilter.class);
	public static final String NAME = "default";

	@Override public Object invoke(final Environment environment,final Object piped,final Object[] params)
	{
		if (L.isTraceEnabled()) {
			L.trace(piped + " | default(" + params[0] + ")");
		}
		if (piped != null && !(piped instanceof Null)) {
			return piped;
		}
		return params[0];
	}
}
