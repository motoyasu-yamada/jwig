/*
 * This file is part of Jwig (Twig for Java).
 *
 * (c) 2010 Spicysoft Corporation. Motoyasu Yamada
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.spicysoft.jwig.loader;

import com.spicysoft.jwig.Loader;

/**
 * Loads a template from a string.
 *
 * When using this loader with a cache mechanism, you should know that a new cache
 * key is generated each time a template content "changes" (the cache key being the
 * source code of the template). If you don't want to see your cache grows out of
 * control, you need to take care of clearing the old cache file by yourself.
 *
 * @author     Motoyasu Yamada
 * @version    $Id$
 */
public class StringLoader implements Loader
{
	@Override public String getSource(final String name)
    {
        return name;
    }

    @Override public String getCacheKey(final String name)
    {
        return name;
    }

    @Override public boolean isFresh(final String name,long time)
    {
        return true;
    }
}
