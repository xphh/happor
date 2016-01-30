package cn.lechange.happor;

import cn.lechange.happor.context.HapporManualContext;

public class TestWithoutSpring {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HapporContext context = new HapporManualContext();
		
		context.setWebserverHandler(new TestWebserverHandler())
				.addController(TestIncomingFilter.class, null, ".*")
				.addController(TestNormalHandler.class, "GET", "^/test/(\\w+)")
				.addController(TestAsyncHandler.class, "GET", "^/async")
				.addController(TestTransitHandler.class, "GET", "^/trans")
				.addController(DefaultHandler.class);

		context.getServer().setPort(9080);
		context.getServer().setExecuteThreads(16);
		context.getServer().setMaxHttpSize(1000000);
		context.getServer().setTimeout(30);

		context.runServer();
	}

}
