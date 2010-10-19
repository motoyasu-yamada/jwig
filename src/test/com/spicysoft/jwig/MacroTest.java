package com.spicysoft.jwig;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MacroTest extends TemplateTest
{
	@Test(dataProvider = "templates")
	public void test(final String src,final String out,final String json) throws Exception
	{
		super.test(src, out, json);
	}

	@DataProvider(name="templates")
	public Object[][] test()
	{
		return new String[][] {
			{"/macro/self.html",        "/macro/self.out.html",        "/macro/variables.json"},
			{"/macro/import_forms.html","/macro/import_forms.out.html","/macro/variables.json"},
			{"/macro/wrapped.html",     "/macro/wrapped.out.html",     "/macro/variables.json"},
			{"/macro/shortcuted.html",  "/macro/shortcuted.out.html",  "/macro/variables.json"}
		};
	}
}
