package com.spicysoft.jwig;

import com.spicysoft.jwig.renderer.WalkingContext;


@SuppressWarnings("serial")
public final class JwigRuntimeException extends RuntimeException
{
	private final String msg;
	private final Throwable cause;

	public JwigRuntimeException(final WalkingContext context,final String msg,final Throwable cause)
	{
		this.msg   = (msg != null ? msg : "") + (context != null ? context.getCurrentLocationString() : "");
		this.cause = cause;
	}

	public JwigRuntimeException(final WalkingContext context,final String msg)
	{
		this(context,msg,(Throwable)null);
	}

	public JwigRuntimeException(final WalkingContext context,final String format,final Object ... args)
	{
		this(context,String.format(format, args),(Throwable)null);
	}

	public JwigRuntimeException(final WalkingContext context,final Throwable cause)
	{
		this(context,null,cause);
	}

	public JwigRuntimeException(final String msg,final Throwable cause)
	{
		this(null,msg,cause);
	}

	public JwigRuntimeException(final String msg)
	{
		this(null,msg,(Throwable)null);
	}

	public JwigRuntimeException(final String format,final Object ... args)
	{
		this(null,String.format(format, args),(Throwable)null);
	}

	public JwigRuntimeException(final Throwable cause)
	{
		this(null,null,cause);
	}

	@Override public Throwable getCause() {
		return cause;
	}

	@Override public String getMessage() {
		return msg;
	}

}
