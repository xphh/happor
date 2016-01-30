package cn.lechange.happor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.NullEnumeration;

import cn.lechange.happor.annotation.Controller;
import cn.lechange.happor.controller.HttpController;

public abstract class HapporContext {

	private static Logger logger = Logger.getLogger(HapporContext.class);

	private HapporWebserver server;
	private Map<String, ControllerRegistry> controllers = new LinkedHashMap<String, ControllerRegistry>();
	private WebserverHandler webserverHandler;

	static {
		if (LogManager.getRootLogger().getAllAppenders() instanceof NullEnumeration) {
			BasicConfigurator.configure();
			LogManager.getRootLogger().setLevel(Level.INFO);
		}
	}

	public void runServer() {
		for (Map.Entry<String, ControllerRegistry> entry : controllers
				.entrySet()) {
			logger.info(entry.getKey() + "[" + entry.getValue().getMethod()
					+ " " + entry.getValue().getUriPattern() + "]");
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

	public Map<String, ControllerRegistry> getControllers() {
		return controllers;
	}

	public HapporContext addController(Class<? extends HttpController> clazz,
			String method, String uriPattern) {
		String name = "controller#" + controllers.size() + "_" + clazz.getName();
		ControllerRegistry registry = new ControllerRegistry();
		registry.setClassName(clazz.getName());
		registry.setMethod(method);
		registry.setUriPattern(uriPattern);
		controllers.put(name, registry);
		return this;
	}

	public HapporContext addController(Class<? extends HttpController> clazz) {
		if (clazz.isAnnotationPresent(Controller.class)) {
			Controller anno = clazz.getAnnotation(Controller.class);
			addController(clazz, anno.method(), anno.uriPattern());
		} else {
			addController(clazz, null, null);
		}
		return this;
	}

	public HapporContext addController(ControllerRegistry registry) {
		String name = "controller#" + controllers.size() + "_" + registry.getClassName();
		controllers.put(name, registry);
		return this;
	}

	public void clearControllers() {
		controllers.clear();
	}

	public abstract HttpController getController(Class<? extends HttpController> clazz);

}
