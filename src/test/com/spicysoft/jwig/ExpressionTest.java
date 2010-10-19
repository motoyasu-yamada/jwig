package com.spicysoft.jwig;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ExpressionTest extends TemplateTest
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
			{"/expressions/literal.html",   "/expressions/literal.out.html",   null},
			{"/expressions/math.html",      "/expressions/math.out.html",      null},
			{"/expressions/logic.html",     "/expressions/logic.out.html",     null},
			{"/expressions/comparison.html","/expressions/comparison.out.html",null},
			{"/expressions/other.html",     "/expressions/other.out.html",     "/expressions/values.json"},
		};
	}
}
