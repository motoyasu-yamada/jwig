package com.spicysoft.jwig.filter;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.spicysoft.jwig.Environment;
import com.spicysoft.jwig.Filter;

/**
 * The date filter is able to format a date to a given format:
 * {{ post.published_at|date("m/d/Y") }}
 * The date filter accepts any date format supported by DateTime and DateTime instances.
 * @see http://www.twig-project.org/book/02-Twig-for-Template-Designers
 */
public class DateFilter implements Filter
{
	public static final String NAME = "date";

	@Override public Object invoke(final Environment environment,final Object piped,final Object[] params)
	{
		if (!(piped instanceof Date)) {
			throw new IllegalArgumentException();
		}

		final Date date = (Date)piped;
		final String pattern = params[0].toString();
		final SimpleDateFormat format = new SimpleDateFormat(pattern,environment.getLocale());
		return format.format(date);
	}
}
