package com.spicysoft.jwig.renderer;

import java.util.List;

import com.spicysoft.jwig.sablecc.node.Node;

final class Macro
{
	public final List<String> args;
	public final Node node;

	public Macro(final List<String> args,final Node node)
	{
		this.args = args;
		this.node = node;
	}
}
