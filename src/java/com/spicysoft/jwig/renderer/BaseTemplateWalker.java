package com.spicysoft.jwig.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spicysoft.jwig.JwigRuntimeException;
import com.spicysoft.jwig.Variables;
import com.spicysoft.jwig.sablecc.node.AAdditionalMacroArglist;
import com.spicysoft.jwig.sablecc.node.AAssignmentStatement;
import com.spicysoft.jwig.sablecc.node.ABlockStart;
import com.spicysoft.jwig.sablecc.node.ADebugStatement;
import com.spicysoft.jwig.sablecc.node.ADisplayStatement;
import com.spicysoft.jwig.sablecc.node.AFilterStatement;
import com.spicysoft.jwig.sablecc.node.AForBlock;
import com.spicysoft.jwig.sablecc.node.AHtmlTemplateElement;
import com.spicysoft.jwig.sablecc.node.AIfBlock;
import com.spicysoft.jwig.sablecc.node.AImportStatement;
import com.spicysoft.jwig.sablecc.node.AIncludeStatement;
import com.spicysoft.jwig.sablecc.node.AKeyValueForStartStatement;
import com.spicysoft.jwig.sablecc.node.AMacroArglist;
import com.spicysoft.jwig.sablecc.node.AMacroInvokeArgs;
import com.spicysoft.jwig.sablecc.node.AMacroStart;
import com.spicysoft.jwig.sablecc.node.AMacroStatement;
import com.spicysoft.jwig.sablecc.node.ANormalAssignmentVarToSet;
import com.spicysoft.jwig.sablecc.node.ANormalBlockStatement;
import com.spicysoft.jwig.sablecc.node.ANormalForStartStatement;
import com.spicysoft.jwig.sablecc.node.AParentStatement;
import com.spicysoft.jwig.sablecc.node.APrintStatement;
import com.spicysoft.jwig.sablecc.node.AShortcutBlockStatement;
import com.spicysoft.jwig.sablecc.node.AVoidMacroInvoke;
import com.spicysoft.jwig.sablecc.node.AWithArgsMacroInvoke;
import com.spicysoft.jwig.sablecc.node.Node;
import com.spicysoft.jwig.sablecc.node.PTemplateElement;


public class BaseTemplateWalker extends ExpressionEvaluationWalker
{
	private static final Log L = LogFactory.getLog(BaseTemplateWalker.class);
    static void walk(final Node root,final RenderContext context)
    {
    	if (root == null) {
    		throw new IllegalArgumentException("Null Argument: root");
    	}
    	if (context == null) {
    		throw new IllegalArgumentException("Null Argument: context");
    	}
    	context.renderStack().mark();
        root.apply(new BaseTemplateWalker(context));
        context.renderStack().checkMark(0);
    }

    private BaseTemplateWalker(final RenderContext context)
    {
    	super(L,context);
    }

    private final void output(final String output)
    {
    	this.context.output(output);
    }

	@Override public void caseAImportStatement(AImportStatement node)
	{
		super.inAImportStatement(node);

        node.getExpression().apply(this);

        final String as = node.getIdent().getText();

        final Macros macros;
        if (context.variables().contains(as,Macros.class)) {
        	macros = (Macros)context.variables().get(as);
        } else {
        	if (as == null) {
        		macros = new Macros();
        	} else {
        		macros = (Macros)context.variables().get("_self");
        	}
        	context.variables().put(as, macros);
        }

        final Object templateObject = context.renderStack().pop();
        final Node loaded;
        if (templateObject instanceof String) {
        	loaded = context.environment().getNode((String)templateObject);
        } else {
        	throw new JwigRuntimeException(this, "include templateObject:'%s' should be instance of String or Template, but is:%s",templateObject,templateObject.getClass());
        }

        final Macros saved = context.setCurrentMacros(macros);
        final RenderWalker walker = new RenderWalker(context);
        walker.render(loaded);
        context.setCurrentMacros(saved);

		super.outAImportStatement(node);
	}

	@Override public void caseAMacroStatement(AMacroStatement node)
	{
		super.inAMacroStatement(node);

		final AMacroStart ms = (AMacroStart)node.getMacroStart();
        final String name = ms.getIdent().getText();
        final List<String> args = new ArrayList<String>();
        extractMacroArglist((AMacroArglist)ms.getMacroArglist(),args);
        final Macro macro = new Macro(args,node.getTemplateElements());

        if (L.isTraceEnabled()) {
        	L.trace(String.format("name='%s',macro='%s'",name,macro));
        }
        this.context.currentMacros().add(name, macro);

		super.outAMacroStatement(node);
	}

	@SuppressWarnings("unchecked")
	private static final void extractMacroArglist(final AMacroArglist ma,final List<String> args)
	{
		args.add(ma.getIdent().getText());

		for (final AAdditionalMacroArglist ama : (List<AAdditionalMacroArglist>)ma.getAdditionalMacroArglist()) {
			args.add(ama.getIdent().getText());
		}
	}

    @Override public void caseAVoidMacroInvoke(AVoidMacroInvoke node)
    {
    	super.inAVoidMacroInvoke(node);

    	context.renderStack().mark();

    	node.getPrimary().apply(this);
    	invokeMacro(node.getIdent().getText(), 0);

    	context.renderStack().checkMark(1);

    	super.outAVoidMacroInvoke(node);
    }

    @Override public void caseAWithArgsMacroInvoke(AWithArgsMacroInvoke node)
    {
    	super.inAWithArgsMacroInvoke(node);

    	context.renderStack().mark();

    	final AMacroInvokeArgs args = ((AMacroInvokeArgs)node.getMacroInvokeArgs());
    	args.apply(this);
    	node.getPrimary().apply(this);
    	final int passed = args.getAdditionalMacroInvokeArgs().size() + 1;
    	invokeMacro(node.getIdent().getText(), passed);

    	context.renderStack().checkMark(1);

    	super.outAWithArgsMacroInvoke(node);
    }

	private void invokeMacro(final String name, final int passed)
	{
		if (L.isTraceEnabled()) {
			L.trace("invokeMacro name:'" + name + "',passed=" + passed);
		}
		final Macros macros = (Macros)context.renderStack().pop();
    	final Macro macro   = macros.get(name);

    	for (int n = macro.args.size() - 1; 0 <= n;n--) {
    		final String key = macro.args.get(n);
    		final Object value;
    		if (n < passed) {
    			value = context.renderStack().pop();
    		} else {
    			value = Null.INSTANCE;
    		}
    		if (L.isTraceEnabled()) {
    			L.trace("[" + n + "]" + key + "=>" + value);
    		}
    		context.variables().put(key, value);
    	}

    	final StringBuffer saved = context.startCaptureRenderingBuffer();
    	macro.node.apply(this);
    	final String captured = context.endCaptureRenderingBuffer(saved);
    	context.renderStack().push(captured);
		if (L.isTraceEnabled()) {
			L.trace("Captured Macro:" + captured);
		}
	}

	@Override public void caseANormalBlockStatement(ANormalBlockStatement node)
	{
		super.inANormalBlockStatement(node);

		final String ident = ((ABlockStart)node.getBlockStart()).getIdent().getText();

		context.applyBlock(ident,this);

		super.outANormalBlockStatement(node);
	}

	@Override public void caseAShortcutBlockStatement(AShortcutBlockStatement node)
	{
		super.inAShortcutBlockStatement(node);

		final String ident = node.getIdent().getText();

		context.applyBlock(ident,this);

		super.outAShortcutBlockStatement(node);
	}



	@Override public void caseAParentStatement(AParentStatement node)
	{
        inAParentStatement(node);

        context.applyParent(this);

        outAParentStatement(node);
	}

	@Override public void outAHtmlTemplateElement(AHtmlTemplateElement node)
    {
		output(node.getHtml().getText());
        super.outAHtmlTemplateElement(node);
    }

    @Override public void outAPrintStatement(APrintStatement node)
    {
    	output(context.renderStack().pop().toString());
    	super.outAPrintStatement(node);
    }

    @Override public void outADisplayStatement(ADisplayStatement node)
    {
    	output(context.renderStack().pop().toString());
        super.outADisplayStatement(node);
    }

    @Override public void caseAForBlock(AForBlock node)
    {
        inAForBlock(node);
        context.renderStack().mark();

        node.getForStartStatement().apply(this);

        final Loop loop = (Loop)context.renderStack().pop();
        context.setCurrentLoop(loop);
        context.variables().put("loop", loop);

        for (final Object current : loop){
        	loop.onPerLoop(context.variables(), current);
        	node.getTemplateElements().apply(this);
        }
        loop.onAfterLoop(context.variables());

        if(loop.isEmpty() && node.getForElseBlock() != null) {
            node.getForElseBlock().apply(this);
        }
        context.variables().remove("loop");
        if (loop.parent != null) {
        	context.setCurrentLoop(loop.parent);
        	context.variables().put("loop", loop.parent);
        }

        context.renderStack().checkMark(0);

        outAForBlock(node);
    }

    @Override public void caseANormalForStartStatement(ANormalForStartStatement node)
    {
    	inANormalForStartStatement(node);
        node.getExpression().apply(this);
        context.renderStack().push(new NormalLoop(context.getCurrentLoop(),context.renderStack().pop(),node.getIdent().getText()));
        outANormalForStartStatement(node);
    }

    @Override public void caseAKeyValueForStartStatement(AKeyValueForStartStatement node)
    {
    	inAKeyValueForStartStatement(node);

    	node.getExpression().apply(this);
    	context.renderStack().push(new KeyValueLoop(context.getCurrentLoop(),context.renderStack().pop(),node.getKey().getText(),node.getValue().getText()));

    	outAKeyValueForStartStatement(node);
    }

    @Override public void caseAIfBlock(AIfBlock node)
    {
        inAIfBlock(node);
        node.getIfStatement().apply(this);
        for(;;) {
	        if (Op.evaluate(context.renderStack().pop())) {
	        	for (Object child : node.getThenBlock()) {
	        		((Node)child).apply(this);
	        	}
	            break;
	        }
	        if(node.getElseBlock() != null) {
	            node.getElseBlock().apply(this);
	        }
	        break;
        }
        outAIfBlock(node);
    }

    @Override public void caseADebugStatement(ADebugStatement node)
    {
        inADebugStatement(node);
        final Object todump;
        if(node.getIdent() != null) {
        	todump = context.variableAccessor().accessByDot(context.variables(), node.getIdent().getText());
        } else {
        	todump = context.variables();
        }
        output(todump.toString());
        outADebugStatement(node);
    }

    @Override public void caseAAssignmentStatement(AAssignmentStatement node)
    {
        inAAssignmentStatement(node);

        node.getAssignmentRight().apply(this);
        node.getAssignmentLeft().apply(this);

        outAAssignmentStatement(node);
    }

    @Override public void outANormalAssignmentVarToSet(ANormalAssignmentVarToSet node)
    {
        context.variableAccessor().setBySubscript(context.variables(),node.getIdent().getText(),context.renderStack().pop());
        super.outANormalAssignmentVarToSet(node);
    }

//    @Override public void outADotAssignmentVarToSet(ADotAssignmentVarToSet node)
//    {
//    	final Object that = context.renderStack().pop();
//    	context.variableAccessor().setByDot(that,node.getIdent().getText(),context.renderStack().pop());
//        super.outADotAssignmentVarToSet(node);
//    }
//
//    @Override public void outASubscriptAssignmentVarToSet(ASubscriptAssignmentVarToSet node)
//    {
//    	final Object that = context.renderStack().pop();
//    	final String key  = (String)context.renderStack().pop();
//    	context.variableAccessor().setBySubscript(that,key,context.renderStack().pop());
//        super.outASubscriptAssignmentVarToSet(node);
//    }

    @SuppressWarnings("unchecked")
    @Override public void caseAIncludeStatement(AIncludeStatement node)
    {
        inAIncludeStatement(node);

        node.getExpression().apply(this);
        final Object templateObject = context.renderStack().pop();

        final Variables variablesToPass;
        if(node.getIncludeWithArgs() != null) {
            node.getIncludeWithArgs().apply(this);
            final Map<String,Object> tmp =
            	(Map<String,Object>)context.renderStack().pop();
            variablesToPass = new Variables(tmp);
        } else {
        	variablesToPass = context.variables();
        }

    	final Node included = context.environment().getNode((String)templateObject);
    	final Variables saved = context.setVariables(variablesToPass);
    	included.apply(this);
    	context.setVariables(saved);
        outAIncludeStatement(node);
    }

    @Override public void caseAFilterStatement(AFilterStatement node)
    {
        inAFilterStatement(node);

        final StringBuffer saved = context.startCaptureRenderingBuffer();
        {
            Object temp[] = node.getTemplateElement().toArray();
            for(int i = 0; i < temp.length; i++)
            {
                ((PTemplateElement) temp[i]).apply(this);
            }
        }
        final String captured = context.endCaptureRenderingBuffer(saved);
        context.renderStack().push(captured);
        node.getFilterStart().apply(this);

        output(context.renderStack().pop().toString());

        outAFilterStatement(node);
    }

}
