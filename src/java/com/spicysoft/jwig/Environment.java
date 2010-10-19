package com.spicysoft.jwig;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spicysoft.jwig.filter.CycleFilter;
import com.spicysoft.jwig.filter.DateFilter;
import com.spicysoft.jwig.filter.DefaultFilter;
import com.spicysoft.jwig.filter.EscapeFilter;
import com.spicysoft.jwig.filter.FormatFilter;
import com.spicysoft.jwig.filter.NullFilter;
import com.spicysoft.jwig.filter.UrlencodeFilter;
import com.spicysoft.jwig.sablecc.lexer.Lexer;
import com.spicysoft.jwig.sablecc.lexer.LexerException;
import com.spicysoft.jwig.sablecc.node.Node;
import com.spicysoft.jwig.sablecc.parser.Parser;
import com.spicysoft.jwig.sablecc.parser.ParserException;

public final class Environment
{
	private static final Log L = LogFactory.getLog(Environment.class);
	private static final Locale DEFAULT_LOCALE = Locale.JAPAN;
	private static final Charset DEFAULT_URL_CHARSET = Charset.forName("UTF8");
	private final Loader loader;
	private final Map<String,Filter>   registeredFilters;
	private Locale locale;
	private Charset urlCharset;

	public Environment(final Loader loader)
	{
		if (loader == null) {
			throw new IllegalArgumentException("Null Argument: loader");
		}
		this.loader            = loader;
		this.locale            = DEFAULT_LOCALE;
		this.urlCharset        = DEFAULT_URL_CHARSET;
		this.registeredFilters = new HashMap<String,Filter>();

		registerCoreFilters();
	}

	public final Locale getLocale()
	{
		return this.locale;
	}

	public final Charset getURLCharset()
	{
		return urlCharset;
	}

	public Template loadTemplate(final String param) throws JwigErrorException
	{
		try {
			final Template template = new Template(this,getNode(param));
	        return template;
		} catch(final JwigRuntimeException e) {
			throw new JwigErrorException(e);
		}
	}

	public final Node getNode(final String param)
	{
		if (param == null) {
			throw new IllegalArgumentException();
		}

		final String string = this.loader.getSource(param);
        assert(string != null);

        final PushbackReader reader = new PushbackReader(new StringReader(string),1024);
        final Lexer  lexer  = new Lexer(reader);
        final Parser parser = new Parser(lexer);
        final Node root;
        try {
        	root = parser.parse();
        } catch(final IOException e) {
        	throw new JwigRuntimeException("param:" + param,e);
        } catch (ParserException e) {
        	throw new JwigRuntimeException("param:" + param,e);
		} catch (LexerException e) {
			throw new JwigRuntimeException("param:" + param,e);
		} finally {
			try {
				reader.close();
			} catch (final IOException e) {
				L.error("param:" + param, e);
			}
		}
        return root;
	}

	public void registerCoreFilters()
	{
		registerFilter(DateFilter.class);
		registerFilter(FormatFilter.class);
		registerFilter(CycleFilter.class);
		registerFilter(UrlencodeFilter.class);
		registerFilter(DefaultFilter.class);
		registerFilter(EscapeFilter.class);
	}

	public final void registerFilter(final Class<? extends Filter> filterClass)
	{
		if (filterClass == null) {
			throw new IllegalArgumentException("Null Argument: filterClass");
		}
		final Filter filter;
		try {
			filter = filterClass.newInstance();
		} catch(final IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch(final InstantiationException e) {
			throw new IllegalArgumentException(e);
		}

		String[] names = null;
		try {
			final Method getNames = filterClass.getMethod("getNames");
			names = (String[])getNames.invoke(null);
		} catch(final NoSuchMethodException e) {
			L.trace(e);
			names = null;
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}

		if (names == null) {
			try {
				final Field NAME = filterClass.getField("NAME");
				names = new String[] {(String)NAME.get(null)};
			} catch(final NoSuchFieldException e) {
				throw new IllegalArgumentException("No Filter Name Accessor" + filterClass,e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}

		for (final String name : names) {
			registerFilter(name,filter);
		}
	}

	public final void registerFilter(final String name,final Filter filter)
	{
		if (name == null) {
			throw new IllegalArgumentException("Null Argument: name");
		}
		if (filter == null) {
			throw new IllegalArgumentException("Null Argument: filter");
		}

		if (registeredFilters.containsKey(name)) {
			final Filter to = registeredFilters.get(name);
			L.info(String.format("Overwrite Filter(%s,%s => %s)",name,to,filter));
			registeredFilters.remove(name);
		}
		L.info(String.format("registerFilter(%s,%s)",name,filter));
		this.registeredFilters.put(name,filter);
	}

	public final Filter getRegisteredFilter(final String name)
	{
		if (name == null) {
			throw new IllegalArgumentException("Null Argument: name");
		}
		if (!registeredFilters.containsKey(name)) {
			L.error(String.format("Filter('') doesn't exist.",name));
			return NullFilter.INSTANCE;
		}
		return registeredFilters.get(name);
	}

}
