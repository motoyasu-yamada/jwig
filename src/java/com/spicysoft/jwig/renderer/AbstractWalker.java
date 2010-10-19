package com.spicysoft.jwig.renderer;

import org.apache.commons.logging.Log;

import com.spicysoft.jwig.sablecc.analysis.DepthFirstAdapter;
import com.spicysoft.jwig.sablecc.node.Node;
import com.spicysoft.jwig.sablecc.node.Token;

abstract class AbstractWalker extends DepthFirstAdapter implements WalkingContext
{
	protected final Log L;
	private Token currentToken;

	protected AbstractWalker(final Log L)
	{
		super();
		this.L = L;
	}

	@Override public void defaultCase(Node node)
	{
		defaultIn(node);
		defaultOut(node);
	}

	@Override public void defaultIn(Node node)
	{
		if (!L.isTraceEnabled()) {
			return;
		}
		L.trace("IN:" + node.getClass() + ":" + node);
		if (!(node instanceof Token)) {
			return;
		}
		currentToken = (Token)node;
	}

	@Override public void defaultOut(Node node)
	{
		if (!L.isTraceEnabled()) {
			return;
		}
		L.trace("OUT:" + node.getClass() + ":" + node);
	}

	@Override public final String getCurrentLocationString()
	{
		if (currentToken != null) {
			final int line = currentToken.getLine();
			final int pos  = currentToken.getPos();
			final String text = currentToken.getText();
			final String assumed = text.substring(0,Math.min(16,text.length()));
			return String.format(" on line %d, pos %d assumed(%s)",line,pos,assumed);
		} else {
			return " on unknown line";
		}
	}
}