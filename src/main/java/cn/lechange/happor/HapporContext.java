package cn.lechange.happor;

import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.NullEnumeration;

import cn.lechange.happor.controller.HttpController;

public abstract class HapporContext {

	private static Logger logger = Logger.getLogger(HapporContext.class);

	private HapporWebserver server;
	private Map<String, HttpController> controllers;
	private WebserverHandler webserverHandler;
	
	static {
		if (LogManager.getRootLogger().getAllAppenders() instanceof NullEnumeration) {
			BasicConfigurator.configure();
			LogManager.getRootLogger().setLevel(Level.INFO);
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
	
	public HapporContext setServer(HapporWebserver server) {
		this.server = server;
		return this;
	}
	
	public HapporWebserver getServer() {
		return server;
	}
	
	public HapporContext setWebserverHandler(WebserverHandler handler) {
		webserverHandler = handler;
		return this;
	}

	public WebserverHandler getWebserverHandler() {
		return webserverHandler;
	}
	
	public HapporContext setControllers(Map<String, HttpController> controllers) {
		this.controllers = controllers;
		return this;
	}

	public Map<String, HttpController> getControllers() {
		return controllers;
	}

	public HapporContext addController(HttpController controller) {
		controllers.put(controller.toString(), controller);
		return this;
	}

	public abstract HttpController getController(String name);
	
}
