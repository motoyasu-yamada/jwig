package com.spicysoft.jwig.renderer;

import java.util.HashMap;

import com.spicysoft.jwig.JwigRuntimeException;

public class Macros
{
	private final HashMap<String,Macro> macros = new HashMap<String,Macro>();

	public final void add(final String name,final Macro macro)
	{
		if (name == null) {
			throw new IllegalArgumentException("Null Argument: name");
		}
		if (macro == null) {
			throw new IllegalArgumentException("Null Argument: macro");
		}
		if (macros.containsKey(name)) {
			throw new JwigRuntimeException("Macro Duplicated:%s",name);
		}
		macros.put(name, macro);
	}

	public final Macro get(final String name)
	{
		if (name == null) {
			throw new IllegalArgumentException("Null Argument: name");
		}
		if (!macros.containsKey(name)) {
			throw new JwigRuntimeException("Macro Not Found:%s",name);
		}
		return macros.get(name);
	}
}
