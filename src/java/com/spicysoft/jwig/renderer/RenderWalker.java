package com.spicysoft.jwig.renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.spicysoft.jwig.Environment;
import com.spicysoft.jwig.Variables;
import com.spicysoft.jwig.sablecc.node.ABaseTemplateTemplate;
import com.spicysoft.jwig.sablecc.node.AChildTemplate;
import com.spicysoft.jwig.sablecc.node.Node;

/**
 * This will switch a walker (RenderWalker or BlockWalker)
 */
public class RenderWalker extends AbstractWalker
{
	private static final Log L = LogFactory.getLog(RenderWalker.class);
	private final RenderContext context;

	public static String render(final Environment environment,final Node root,final Variables values)
    {
        final RenderWalker walker = new RenderWalker(environment,values);
        return walker.render(root).getRenderedString();
    }

	private RenderWalker(final Environment environment,final Variables values) {
    	super(L);
    	context = new RenderContext(environment,this,values);
    }

    RenderWalker(final RenderContext context)
    {
    	super(L);
    	this.context = context;
    }

    final RenderContext render(final Node root)
    {
    	context.renderStack().mark();
        root.apply(this);
        context.renderStack().checkMark(0);
        return context;
    }

	@Override public void caseAChildTemplate(AChildTemplate node)
    {
        inAChildTemplate(node);

        ChildTemplateWalker.walk(node,context);
        final String baseTemplate = (String)context.renderStack().pop();

        final Node baseNode = context.environment().getNode((String)baseTemplate);

        final RenderWalker walker = new RenderWalker(context);
        walker.render(baseNode);

        outAChildTemplate(node);
    }

    @Override public void caseABaseTemplateTemplate(ABaseTemplateTemplate node)
    {
        inABaseTemplateTemplate(node);

        ChildTemplateWalker.walkForBase(node,context);

        BaseTemplateWalker.walk(node.getBaseTemplate(), context);

        outABaseTemplateTemplate(node);
    }
}
