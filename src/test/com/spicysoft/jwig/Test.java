package com.spicysoft.jwig;

import java.math.BigDecimal;
import java.sql.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Test {
	private static final Log L = LogFactory.getLog(Test.class);

	public static void main(final String[] args) throws Exception
	{
		if (new BigDecimal(1).equals(1)) {
			System.out.println("true");
		} else {
			System.out.println("false");
		}
//		L.info("START");
//		L.info("Test value init start.");
//        final Map<String,Object> values = new HashMap<String,Object>();
//        {
//	        final List<Object> list = new ArrayList<Object>();
//	        Object item1 = new Item("JavaにGoto文がある？ラベルで多重にネストしたループを抜け出す","http://yamarou.at.infoseek.co.jp/javanawake/034.html");
//	        list.add(item1);
//	        final Map<String,String> item2 = new HashMap<String,String>();
//	        item2.put("created", "2010/1/1 00:00:00");
//	        item2.put("caption","jwig");
//	        item2.put("href", "http://www.twig-project.org/documentation");
//	        list.add(item2);
//	        values.put("navigation",list);
//	        values.put("a_variable","Jwig is Java Twig by Spicysoft Corp.");
//	        values.put("defaultItem", item2);
//        }
//        values.put("currentDatetime", new Date(System.currentTimeMillis()));
//        L.info("Test value init end.");
//
//        final Environment twig = new Environment(new FilesystemLoader("src/test/"));
//        final Template template = twig.loadTemplate("test.html");
//        template.display(values);
    }

    static class Item {
    	public final Date created;
    	private final String caption;
    	private final String href;

    	public Item(final String caption,final String href) {
    		this.created = new Date(System.currentTimeMillis());
    		this.caption = caption;
    		this.href    = href;
    	}

    	public String href() {
    		return this.href;
    	}

    	public String getCaption() {
    		return this.caption;
    	}
    }
}
