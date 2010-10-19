/*
 * This file is part of Jwig (Twig for Java).
 *
 * (c) 2010 Spicysoft Corporation. Motoyasu Yamada
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.spicysoft.jwig.loader;

import java.util.Map;

import com.spicysoft.jwig.Loader;

/**
 * Loads a template from an array.
 *
 * When using this loader with a cache mehcanism, you should know that a new cache
 * key is generated each time a template content "changes" (the cache key being the
 * source code of the template). If you don't want to see your cache grows out of
 * control, you need to take care of clearing the old cache file by yourself.
 *
 * @author     Motoyasu Yamada
 * @version    $Id$
 */
public class ArrayLoader implements Loader
{
    private final Map<String,String> templates;

    /**
     * Constructor.
     *
     * @param templates An array of templates (keys are the names, and values are the source code)
     *
     * @see Loader
     */
    public ArrayLoader(final Map<String,String> templates,final Class<Map<String,String>> klass)
    {
		try {
			this.templates = klass.newInstance();
		} catch (final Exception e) {
			throw new IllegalArgumentException("Class specifed by 'klass'("
					+ klass + ") can't instanciate.", e);
		}
		for (Map.Entry<String, String> set : templates.entrySet()) {
			this.templates.put(set.getKey(), set.getValue());
		}
    }

    @SuppressWarnings("unchecked")
    public ArrayLoader(final Map<String,String> templates)
    {
    	this(templates,(Class<Map<String,String>>)templates.getClass());
    }

    @Override public String getSource(final String name)
    {
        if (!this.templates.containsKey(name)) {
            throw new IllegalArgumentException("Template '" + name + "' is not defined.");
        }

        return this.templates.get(name);
    }

    @Override public String getCacheKey(final String name)
    {
    	if (!this.templates.containsKey(name)) {
            throw new IllegalArgumentException("Template '" + name + "' is not defined.");
        }
    	return this.templates.get(name);
    }

    @Override public boolean isFresh(final String name, final long time)
    {
        return true;
    }
}
