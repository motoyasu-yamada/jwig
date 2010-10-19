package com.spicysoft.jwig;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import com.spicysoft.jwig.loader.FilesystemLoader;

abstract class TemplateTest
{
	@SuppressWarnings("unchecked")
	public void test(final String src,final String out,final String json) throws Exception
	{
		final FilesystemLoader loader = new FilesystemLoader("test");
		final Map<String,Object> values =
			json == null ?
				new HashMap<String,Object>() :
				(Map<String,Object>)JSON.decode(loader.getSource(json));
		final String expected = loader.getSource(out);
        final Environment twig = new Environment(loader);
        final Template template = twig.loadTemplate(src);
        final String rendered = template.render(values);
        assertEquals(rendered,expected);
	}

}