package com.spicysoft.lib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public final class Filesystem
{
	public static final String file_get_contents(final String filename,final Charset charset) throws IOException
	{
		final byte[] bytes = file_get_bytes(filename);

		return new String(bytes,charset);
	}

	public static final byte[] file_get_bytes(final String filename) throws IOException
	{
		final FileInputStream i = new FileInputStream(filename);
		final ByteArrayOutputStream o = new ByteArrayOutputStream();
		final byte[] temp = new byte[10240];
		int read;
		while ((read = i.read(temp)) != -1) {
			o.write(temp,0,read);
		}
		return o.toByteArray();
	}

	/**
	 * realpath() throws IOException on failure, e.g. if the file does not exist.
	 */
	public static final String realpath(final String path) throws IOException
	{
		return new File(path).getCanonicalPath();
	}

	/**
	 * Creates a new path from a parent pathname string and a child pathname string.
	 */
	public static final String concatpath(final String parent,final String child)
	{
		return new File(parent,child).getPath();
	}
}
