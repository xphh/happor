package cn.lechange.happor;

import cn.lechange.happor.context.HapporJarContainerContext;

public class TestJarContainer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HapporJarContainerContext context = new HapporJarContainerContext();
		context.load("stub");
		context.getServer().setPort(9080);
		context.runServer();
	}

}
