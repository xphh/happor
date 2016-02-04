package cn.lechange.happor;

import cn.lechange.happor.context.HapporMultipleContext;
import cn.lechange.happor.context.HapporSpringContext;

public class TestMultiple {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HapporContext sub1 = new HapporSpringContext("conf/web.xml");
		HapporContext sub2 = new HapporSpringContext("conf/web.xml");
		HapporContext sub3 = new HapporSpringContext("conf/web.xml");
		
		HapporMultipleContext context = new HapporMultipleContext();
		context.addPath("aaa", sub1);
		context.addPath("bbb", sub2);
		context.setDefault(sub3);
		
		context.getServer().setPort(9080);
		context.runServer();
	}

}
