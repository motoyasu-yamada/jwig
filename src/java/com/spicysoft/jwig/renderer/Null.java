package com.spicysoft.jwig.renderer;

public class Null
{
	public static final Null INSTANCE = new Null();

	private Null()
	{
	}

	@Override public final String toString()
	{
		return "";
	}

	@Override public boolean equals(final Object right)
	{
		return right != null && right instanceof Null;
	}
}
