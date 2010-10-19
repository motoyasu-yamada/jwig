package com.spicysoft.jwig.renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spicysoft.jwig.sablecc.node.ABlockStart;
import com.spicysoft.jwig.sablecc.node.AImportStatement;
import com.spicysoft.jwig.sablecc.node.AMacroStatement;
import com.spicysoft.jwig.sablecc.node.ANormalBlockStatement;
import com.spicysoft.jwig.sablecc.node.APrintStatement;
import com.spicysoft.jwig.sablecc.node.AShortcutBlockStatement;
import com.spicysoft.jwig.sablecc.node.AVoidMacroInvoke;
import com.spicysoft.jwig.sablecc.node.AWithArgsMacroInvoke;
import com.spicysoft.jwig.sablecc.node.Node;

final class ChildTemplateWalker extends ExpressionEvaluationWalker
{
	private final static Log L = LogFactory.getLog(ChildTemplateWalker.class);

	private static void innerWalk(final Node node,final RenderContext context,final int stackOffset)
	{
		assert(stackOffset == 0 || stackOffset == 1);
    	if (node == null) {
    		throw new IllegalArgumentException("Null Argument: node");
    	}
    	if (context == null) {
    		throw new IllegalArgumentException("Null Argument: stackOffset");
    	}

    	context.newRenderContext();
    	context.renderStack().mark();
    	final ChildTemplateWalker walker = new ChildTemplateWalker(context);
    	node.apply(walker);
    	// There is the path of the template in the stack.
        context.renderStack().checkMark(stackOffset);
    }

	static void walk(final Node node,final RenderContext context)
    {
		innerWalk(node,context,1);
    }

	static void walkForBase(final Node node,final RenderContext context)
	{
		innerWalk(node,context,0);
	}

	ChildTemplateWalker(final RenderContext context)
	{
		super(L,context);
	}

	@SuppressWarnings("unchecked")
	@Override public void outANormalBlockStatement(ANormalBlockStatement node)
	{
		final String ident = ((ABlockStart)node.getBlockStart()).getIdent().getText();

		context.addNormalBlock(ident, node.getInnerBlockElement());

		super.outANormalBlockStatement(node);
	}

	@Override public void outAShortcutBlockStatement(AShortcutBlockStatement node)
	{
		final String ident = node.getIdent().getText();

		context.addShortcutBlock(ident, node.getExpression());

		super.outAShortcutBlockStatement(node);
	}

	@Override public void caseAImportStatement(AImportStatement node)
	{
		super.inAImportStatement(node);
		super.outAImportStatement(node);
	}

	@Override public void caseAMacroStatement(AMacroStatement node)
	{
		super.inAMacroStatement(node);
		super.outAMacroStatement(node);
	}

    @Override public void caseAVoidMacroInvoke(AVoidMacroInvoke node)
    {
    	super.inAVoidMacroInvoke(node);
    	context.renderStack().push(Null.INSTANCE);
    	super.outAVoidMacroInvoke(node);
    }

    @Override public void caseAWithArgsMacroInvoke(AWithArgsMacroInvoke node)
    {
    	super.inAWithArgsMacroInvoke(node);
    	context.renderStack().push(Null.INSTANCE);
    	super.outAWithArgsMacroInvoke(node);
    }

    @Override public void outAPrintStatement(APrintStatement node)
    {
    	context.renderStack().pop();
    	super.outAPrintStatement(node);
    }
}
