package com.spicysoft.jwig;

import java.util.Map;

import com.spicysoft.jwig.renderer.RenderWalker;
import com.spicysoft.jwig.sablecc.node.Node;

public class Template
{
	private final Node root;
	private final Environment environment;

	Template(final Environment environment,final Node root)
	{
		this.root = root;
		this.environment = environment;
	}

	public void display(final Map<String,Object> values)
	{
		final String encoded = render(values);
		System.out.println(encoded);
	}

    /**
     * Renders the template with the given context and returns it as string.
     *
     * @param variables A map of parameters to pass to the template
     *
     * @return The rendered template
     */
	public String render(final Map<String,Object> variables)
	{
		return RenderWalker.render(this.environment,this.root,new Variables(variables));
	}


}
