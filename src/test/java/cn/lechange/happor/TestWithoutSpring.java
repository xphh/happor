package cn.lechange.happor;

public class TestWithoutSpring {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HapporContext context = new HapporContext();
		context.getServer().setPort(9080);
		context.getServer().setExecuteThreads(16);
		context.getServer().setMaxHttpSize(1000000);
		context.getServer().setTimeout(30);

		TestIncomingFilter testIncomingFilter = new TestIncomingFilter();
		testIncomingFilter.setUriPattern(".*");

		TestNormalHandler testNormalHandler = new TestNormalHandler();
		testNormalHandler.setMethod("GET");
		testNormalHandler.setUriPattern("^/test");

		TestAsyncHandler testAsyncHandler = new TestAsyncHandler();
		testAsyncHandler.setMethod("GET");
		testAsyncHandler.setUriPattern("^/async");

		TestTransitHandler testTransitHandler = new TestTransitHandler();
		testTransitHandler.setMethod("GET");
		testTransitHandler.setUriPattern("^/trans");

		DefaultHandler defaultHandler = new DefaultHandler();
		defaultHandler.setUriPattern(".*");

		context.setWebserverHandler(new TestWebserverHandler())
				.addController(testIncomingFilter)
				.addController(testNormalHandler)
				.addController(testAsyncHandler)
				.addController(testTransitHandler)
				.addController(defaultHandler);

		context.runServer();
	}

}
