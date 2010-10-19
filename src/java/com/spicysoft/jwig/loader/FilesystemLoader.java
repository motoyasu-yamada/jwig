/*
 * This file is part of Jwig (Twig for Java).
 *
 * (c) 2010 Spicysoft Corporation. Motoyasu Yamada
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.spicysoft.jwig.loader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spicysoft.jwig.JwigRuntimeException;
import com.spicysoft.jwig.Loader;
import com.spicysoft.lib.Filesystem;

/**
 * Loads template from the filesystem.
 */
public class FilesystemLoader implements Loader
{
	private static final Log L = LogFactory.getLog(FilesystemLoader.class);
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private List<File> paths;

	/**
	 * Constructor.
	 *
	 * @param string
	 *            |array $paths A path or an array of paths where to look for
	 *            templates
	 */
	public FilesystemLoader(final String path) {
		this.setPaths(new String[] { path });
	}

	/**
	 * Returns the paths to the templates.
	 *
	 * @return The array of paths where to look for templates
	 */
	public File[] getPaths() {
		return (File[]) this.paths.toArray();
	}

	/**
	 * Sets the paths where templates are stored.
	 *
	 * @param string
	 *            |array $paths A path or an array of paths where to look for
	 *            templates
	 */
	public void setPaths(final String[] paths)
	{
		ArrayList<File> pathlist = new ArrayList<File>();
		for (final String path : paths) {
			final File file;
			try {
				file = new File(path).getCanonicalFile();
			} catch (final Exception e) {
				throw new JwigRuntimeException(e);
			}
			if (!file.isDirectory()) {
				throw new JwigRuntimeException("The '" + file
						+ "' directory does not exist.");
			}
			pathlist.add(file);
		}
		this.paths = pathlist;
	}

	@Override public String getSource(final String name) {
		try {
			return Filesystem.file_get_contents(this.findTemplate(name).getPath(),DEFAULT_CHARSET);
		} catch(final IOException e) {
			throw new JwigRuntimeException("Can't access a template named('" + name + "')",e);
		}
	}

	@Override public String getCacheKey(final String name) {
		return this.findTemplate(name).getPath();
	}

	@Override public boolean isFresh(final String name, long time) {
		return this.findTemplate(name).lastModified() < time;
	}

	private final File findTemplate(final String name)
    {
        for (final File root : this.paths) {
        	final File file;
        	try {
        		file = new File(root,name).getCanonicalFile();
        	} catch(final IOException e) {
        		L.error("root:'" + root + "' / name'" + name + "'",e);
        		continue;
        	}
        	if (L.isTraceEnabled()) {
        		L.trace(file);
        	}
        	if (!file.exists()) {
        		continue;
        	}
        	if (!file.getPath().startsWith(root.getPath())) {
        		throw new JwigRuntimeException("Looks like you try to load a template outside configured directories.");
        	}
            return file;
        }

        throw new JwigRuntimeException("Unable to find template name:'%s'.",name);
    }

}
