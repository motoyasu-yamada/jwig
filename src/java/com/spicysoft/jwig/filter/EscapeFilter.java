package com.spicysoft.jwig.filter;

import com.spicysoft.jwig.Environment;
import com.spicysoft.jwig.Filter;

import static com.spicysoft.lib.PString.htmlspecialchars;

/**
 * The escape filter converts the characters &, <, >, ', and "
 * in strings to HTML-safe sequences. Use this if you need to
 * display text that might contain such characters in HTML.
 */
public class EscapeFilter implements Filter
{
	public static final String[] getNames()
	{
		return new String[]{"escape","e"};
	}

	@Override public Object invoke(Environment environment, Object piped, Object[] args)
	{
		return htmlspecialchars(piped.toString());
	}
}
