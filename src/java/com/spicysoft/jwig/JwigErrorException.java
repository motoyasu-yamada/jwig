package com.spicysoft.jwig;

@SuppressWarnings("serial")
public class JwigErrorException extends Exception {
	public JwigErrorException(final JwigRuntimeException e)
	{
		super(e.getMessage(),e);
	}
}
