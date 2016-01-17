package cn.lechange.happor;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cn.lechange.happor.controller.HttpController;

public class HapporHelper {

	private static Logger logger = Logger.getLogger(HapporHelper.class);

	private static FileSystemXmlApplicationContext ctx;

	private static Map<String, HttpController> controllers;

	public static void runServer(String filename) {
		ctx = new FileSystemXmlApplicationContext(filename);

		HapporWebserver server = ctx.getBean(HapporWebserver.class);

		controllers = ctx.getBeansOfType(HttpController.class);
		for (Map.Entry<String, HttpController> entry : controllers.entrySet()) {
			logger.info("add controller: " + entry.getKey() + "["
					+ entry.getValue().getMethod() + " "
					+ entry.getValue().getUriPattern() + "]");
		}

		server.startup();

		ctx.close();
	}

	public static ApplicationContext getContext() {
		return ctx;
	}

	public static Map<String, HttpController> getControllers() {
		return controllers;
	}

	public static HttpController getController(String name) {
		return (HttpController) ctx.getBean(name);
	}

}
