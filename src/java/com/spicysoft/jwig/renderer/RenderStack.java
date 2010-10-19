package com.spicysoft.jwig.renderer;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spicysoft.jwig.JwigRuntimeException;

public final class RenderStack
{
	private static final Log L = LogFactory.getLog(RenderStack.class);
	private final Stack<Object> stack = new Stack<Object>();
	private final Stack<Integer> traceMarker;
	private final WalkingContext walkingContext;

	public RenderStack(WalkingContext walker)
	{
		this.walkingContext = walker;
		if (L.isTraceEnabled()) {
			this.traceMarker = new Stack<Integer>();
			L.info("Trace marker is [ENABLED]");
		} else {
			this.traceMarker = null;
		}
	}

	public void push(final Object object)
	{
		if (object == null) {
			final String msg =
				"Null Pushed : " + toString();
			throw new JwigRuntimeException(walkingContext,msg);
		}
		this.stack.push(object);
		if (L.isTraceEnabled()) {
			L.trace("[push] '" + object + "' in " + toString());
		}
	}

	public Object pop()
	{
		final Object object = this.stack.pop();
		if (L.isTraceEnabled()) {
			L.trace("[pop ] '" + object + "' in "+ toString());
		}
		if (object == null) {
			throw new IllegalStateException("Poped object from stack is null:" + toString());
		}
		return object;
	}

	public void mark()
	{
		if (!L.isTraceEnabled()) {
			return;
		}
		this.traceMarker.push(this.stack.size());
	}

	public void checkMark(final int offset)
	{
		if (!L.isTraceEnabled()) {
			return;
		}
		final int expectedPos = this.traceMarker.pop() + offset;
		final int currentPos  = this.stack.size();
		if (expectedPos != currentPos) {
			L.trace(toString());
			throw new JwigRuntimeException(walkingContext,"currentPos:'" + currentPos + "' is expected as expectedPos:'" + expectedPos);
		}
	}

	public final String toString()
	{
		if (L.isTraceEnabled()) {
			final StringBuffer sb = new StringBuffer();
			sb.append("size:'" + this.stack.size() + "'");
			int n = 0;
			for(final Object i : this.stack) {
				sb.append(",[" + n + "]:'");
				sb.append(i);
				sb.append("'");
				n++;
			}
			return sb.toString();
		} else {
			return this.stack.toString();
		}
	}

}
