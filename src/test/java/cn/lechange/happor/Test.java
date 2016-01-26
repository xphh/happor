package cn.lechange.happor;

import cn.lechange.happor.context.HapporSpringContext;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HapporContext context = new HapporSpringContext("conf/web.xml");
		context.runServer();
	}

}
