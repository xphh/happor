package cn.lechange.happor;

import org.springframework.context.support.FileSystemXmlApplicationContext;

public class HapporHelper {

	public static void runServer(String filename) {
		FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(filename);
		HapporWebserver server = ctx.getBean(HapporWebserver.class);
		server.startup();
		ctx.close();
	}

}
