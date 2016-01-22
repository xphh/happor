package cn.lechange.happor;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cn.lechange.happor.controller.HttpController;

public class HapporContext {

	private static Logger logger = Logger.getLogger(HapporContext.class);

	private FileSystemXmlApplicationContext ctx;

	private Map<String, HttpController> controllers;
	
	private WebserverHandler webserverHandler;

	public HapporContext(String filename) {
		ctx = new FileSystemXmlApplicationContext(filename);

		controllers = ctx.getBeansOfType(HttpController.class);
		for (Map.Entry<String, HttpController> entry : controllers.entrySet()) {
			logger.info("add controller: " + entry.getKey() + "["
					+ entry.getValue().getMethod() + " "
					+ entry.getValue().getUriPattern() + "]");
		}
		
		try {
			webserverHandler = (WebserverHandler) ctx.getBean(WebserverHandler.class);
		} catch (NoSuchBeanDefinitionException e) {
			logger.info("has no WebserverHandler");
		}
	}
	
	public void close() {
		ctx.close();
	}
	
	public void runServer() {
		HapporWebserver server = ctx.getBean(HapporWebserver.class);
		server.startup(this);
	}
	
	public ApplicationContext getContext() {
		return ctx;
	}

	public Map<String, HttpController> getControllers() {
		return controllers;
	}

	public HttpController getController(String name) {
		return (HttpController) ctx.getBean(name);
	}
	
	public WebserverHandler getWebserverHandler() {
		return webserverHandler;
	}

}
