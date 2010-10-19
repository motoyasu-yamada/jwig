package com.spicysoft.jwig.renderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spicysoft.jwig.Environment;
import com.spicysoft.jwig.JwigRuntimeException;
import com.spicysoft.jwig.Variables;
import com.spicysoft.jwig.sablecc.node.PExpression;
import com.spicysoft.jwig.sablecc.node.PInnerBlockElement;
import com.spicysoft.jwig.sablecc.node.Switch;
import com.spicysoft.jwig.sablecc.node.Switchable;

public class RenderContext
{
	private static final Log L = LogFactory.getLog(RenderContext.class);
	private final RenderStack renderStack;
	private final VariableAccessor variableAccessor;
	private final Environment environment;
	private Variables variables;
	private StringBuffer output  = new StringBuffer();
	private Macros currentMacros = new Macros();
	private Loop currentLoop    = null;
	private Block currentBlock  = null;
	private RenderTemplateContext topTemplateContext     = null;
	private RenderTemplateContext currentTemplateContext = null;

	RenderContext(final Environment environment,final WalkingContext context,final Variables variables)
	{
		if (environment == null) {
			throw new IllegalArgumentException("Null Argument: environment");
		}
		if (context == null) {
			throw new IllegalArgumentException("Null Argument: context");
		}
		if (variables == null) {
			throw new IllegalArgumentException("Null Argument: variables");
		}
		this.environment      = environment;
		this.variables        = variables;
		this.renderStack      = new RenderStack(context);
		this.variableAccessor = new VariableAccessor(context);
		this.variables.put("_self", currentMacros);
	}

	public final Macros currentMacros()
	{
		assert(currentMacros != null);
		return this.currentMacros;
	}

	public final Macros setCurrentMacros(final Macros toset)
	{
		if (L.isTraceEnabled()) {
			L.trace(String.format("currentMacros,%s toset = %s",currentMacros,toset));
		}
		if (toset == null) {
			throw new IllegalArgumentException("Null Argument: toset");
		}
		final Macros saved = this.currentMacros;
		this.currentMacros = toset;
		return saved;
	}

	/**
	 * @param loop You can set loop as null, when you go out of all loops.
	 */
	public final void setCurrentLoop(final Loop loop)
	{
		this.currentLoop = loop;
	}

	public final Loop getCurrentLoop()
	{
		//if (this.currentLoop == null) {
		//	throw new IllegalArgumentException("You are out of a loop, so you can't call this method.");
		//}
		return this.currentLoop;
	}

	public final void output(final String output)
	{
		this.output.append(output == null ? "null" : output.toString());
	}

	public final StringBuffer startCaptureRenderingBuffer()
	{
		final StringBuffer saved = this.output;
		if (saved == null) {
			throw new IllegalStateException();
		}
		this.output = new StringBuffer();
		return saved;
	}

	public final String endCaptureRenderingBuffer(final StringBuffer saved)
	{
		if (saved == null) {
			throw new IllegalArgumentException("Null Argument: saved");
		}
		final StringBuffer sub = this.output;
		this.output = saved;
		return sub.toString();
	}

	public final String getRenderedString() {
		return this.output.toString();
	}

	public final Environment environment() {
		return this.environment;
	}

	public final Variables variables() {
		return this.variables;
	}

	public final Variables setVariables(final Variables toset)
	{
		final Variables saved = this.variables;
		this.variables  = toset;
		return saved;
	}

	public final RenderStack renderStack() {
		return this.renderStack;
	}

	public final VariableAccessor variableAccessor() {
		return this.variableAccessor;
	}

	final void applyParent(final Switch sw)
	{
		final Block saved = currentBlock;
		final Block block = getBlock(currentBlock.templateContext.base,currentBlock.name);
		currentBlock = block;
		block.apply(sw);
		currentBlock = saved;
	}

	final void applyBlock(final String name,final Switch sw)
	{
		final Block saved = currentBlock;
		final Block block = getBlock(name);
		currentBlock = block;
		block.apply(sw);
		currentBlock = saved;
	}

	final void addNormalBlock(final String name,final List<PInnerBlockElement> inner)
	{
		currentTemplateContext.addBlock(name,new NormalBlock(name,currentTemplateContext,inner));
	}

	final void addShortcutBlock(final String name,final PExpression inner)
	{
		currentTemplateContext.addBlock(name,new ShortcutBlock(name,currentTemplateContext,inner));
	}

	private final Block getBlock(final String name)
	{
		return getBlock(topTemplateContext,name);
	}

	private final Block getBlock(final RenderTemplateContext context,final String name)
	{
		if (context == null) {
			throw new IllegalArgumentException("Null Argument: context ,name:'" + name + "'");
		}
		if (name == null) {
			throw new IllegalArgumentException("Null Argument: name");
		}
		RenderTemplateContext i = context;
		for (;;) {
			final Block block = i.getBlock(name);
			if (block != null) {
				return block;
			}
			i = i.getBase();
			if (i == null) {
				throw new JwigRuntimeException("Block(name:'%s') isn't found.",name);
			}
		}
	}

	void newRenderContext()
	{
		final RenderTemplateContext newed = new RenderTemplateContext();
		if (currentTemplateContext == null) {
			topTemplateContext = newed;
		} else {
			currentTemplateContext.setBase(newed);
		}
		currentTemplateContext = newed;
	}

	private static abstract class Block implements Switchable
	{
		private final RenderTemplateContext templateContext;
		private final String name;

		protected Block(final String name,final RenderTemplateContext templateContext)
		{
			if (templateContext == null) {
				throw new IllegalArgumentException("Null Argument: templateContext, name:'" + name + "'");
			}
			if (name == null) {
				throw new IllegalArgumentException("Null Argument: name");
			}
			this.templateContext = templateContext;
			this.name = name;
		}
	}

	private static class NormalBlock extends Block
	{
		private final List<PInnerBlockElement> inner;

		NormalBlock(final String name,final RenderTemplateContext templateContext,final List<PInnerBlockElement> inner)
		{
			super(name,templateContext);
			if (inner == null) {
				throw new IllegalArgumentException();
			}
			this.inner = inner;
		}

	    public void apply(Switch sw)
	    {
	    	for (PInnerBlockElement e : inner) {
	    		e.apply(sw);
	    	}
	    }
	}

	private static class ShortcutBlock extends Block
	{
		private final PExpression inner;

		ShortcutBlock(final String name,final RenderTemplateContext templateContext,final PExpression inner)
		{
			super(name,templateContext);
			if (inner == null) {
				throw new IllegalArgumentException();
			}
			this.inner = inner;
		}

	    public void apply(Switch sw)
	    {
	    	inner.apply(sw);
	    }
	}

	private final static class RenderTemplateContext
	{
		private final Map<String,Block> blocks;
		private RenderTemplateContext base;

		RenderTemplateContext()
		{
			this.blocks = new HashMap<String,Block>();
			this.base   = null;
		}

		final void setBase(final RenderTemplateContext base)
		{
			if (base == null) {
				throw new IllegalArgumentException();
			}
			if (this.base != null) {
				throw new IllegalStateException();
			}
			this.base = base;
		}

		final RenderTemplateContext getBase()
		{
			return this.base;
		}

		final void addBlock(final String name,final Block block)
		{
			if (name == null) {
				throw new IllegalArgumentException("Null Argument: name");
			}
			if (block == null) {
				throw new IllegalArgumentException("Null Argument: block");
			}
			if (blocks.containsKey(name)) {
				throw new JwigRuntimeException("Duplicated Block: name='%s'",name);
			}
			blocks.put(name,block);
		}

		final Block getBlock(final String name)
		{
			if (name == null) {
				throw new IllegalArgumentException("Null Argument: name");
			}
			return blocks.get(name);
		}
	}
}
