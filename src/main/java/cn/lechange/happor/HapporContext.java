package cn.lechange.happor;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.NullEnumeration;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cn.lechange.happor.controller.HttpController;

public class HapporContext {

	private static Logger logger = Logger.getLogger(HapporContext.class);

	private FileSystemXmlApplicationContext ctx;
	private HapporWebserver server;
	private Map<String, HttpController> controllers;
	private WebserverHandler webserverHandler;
	
	static {
		if (LogManager.getRootLogger().getAllAppenders() instanceof NullEnumeration) {
			BasicConfigurator.configure();
			LogManager.getRootLogger().setLevel(Level.INFO);
		}
	}

	public HapporContext() {
		this(null);
	}
	
	public HapporContext(String filename) {
		if (filename == null) {
			server = new HapporWebserver();
			controllers = new HashMap<String, HttpController>();
		} else {
			ctx = new FileSystemXmlApplicationContext(filename);
			
			server = ctx.getBean(HapporWebserver.class);
			
			try {
				controllers = ctx.getBeansOfType(HttpController.class);
				webserverHandler = (WebserverHandler) ctx.getBean(WebserverHandler.class);
			} catch (NoSuchBeanDefinitionException e) {
				
			}
		}
	}
	
	public void close() {
		if (ctx != null) {
			ctx.close();
		}
	}
	
	public void runServer() {
		for (Map.Entry<String, HttpController> entry : controllers.entrySet()) {
			logger.info("add controller: " + entry.getKey() + "["
					+ entry.getValue().getMethod() + " "
					+ entry.getValue().getUriPattern() + "]");
		}
		logger.info("webserverHandler = " + webserverHandler);
		server.startup(this);
	}
	
	public ApplicationContext getContext() {
		return ctx;
	}

	public HapporWebserver getServer() {
		return server;
	}
	
	public Map<String, HttpController> getControllers() {
		return controllers;
	}

	public HttpController getController(String name) {
		HttpController controller = null;
		if (ctx != null) {
			controller = (HttpController) ctx.getBean(name);
		} else {
			if (controllers.containsKey(name)) {
				try {
					controller = controllers.get(name).getClass().newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return controller;
	}
	
	public HapporContext addController(HttpController controller) {
		controllers.put(controller.toString(), controller);
		return this;
	}
	
	public WebserverHandler getWebserverHandler() {
		return webserverHandler;
	}
	
	public HapporContext setWebserverHandler(WebserverHandler handler) {
		webserverHandler = handler;
		return this;
	}

}
