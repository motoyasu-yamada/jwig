package com.spicysoft.jwig.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spicysoft.jwig.Environment;
import com.spicysoft.jwig.Filter;

public class UrlencodeFilter implements Filter
{
	private static final Log L = LogFactory.getLog(UrlencodeFilter.class);
	public static final String NAME = "urlencode";

	@Override public Object invoke(Environment environment, Object piped, Object[] params)
	{
		final String string = piped.toString();
		final Charset charset = environment.getURLCharset();
		try {
			final String encoded = URLEncoder.encode(string,charset.name());
			return encoded;
		} catch(final UnsupportedEncodingException e) {
			L.fatal(charset, e);
			throw new IllegalStateException("Can't find encode:'" + charset + "'.", e);
		}
	}
}
