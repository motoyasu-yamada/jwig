package com.spicysoft.jwig;

/**
 * Interface all loaders must implement.
 *
 * @author Motoyasu Yamada
 * @version $Id$
 */
public interface Loader {
	/**
	 * Gets the source code of a template, given its name.
	 *
	 * @param name
	 *            string The name of the template to load
	 *
	 * @return The template source code
	 */
	String getSource(String name);

	/**
	 * Gets the cache key to use for the cache for a given template name.
	 *
	 * @param name
	 *            string The name of the template to load
	 *
	 * @return The cache key
	 */
	String getCacheKey(String name);

	/**
	 * Returns true if the template is still fresh.
	 *
	 * @param name The template name
	 * @param time The last modification time of the cached template
	 */
	boolean isFresh(String name, long time);

}
