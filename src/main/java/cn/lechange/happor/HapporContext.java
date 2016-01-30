package cn.lechange.happor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.NullEnumeration;

import cn.lechange.happor.annotation.Controller;
import cn.lechange.happor.annotation.DefaultController;
import cn.lechange.happor.controller.HttpController;
import cn.lechange.happor.utils.PackageUtil;

public abstract class HapporContext {

	private static Logger logger = Logger.getLogger(HapporContext.class);

	private HapporWebserver server;
	private Map<String, ControllerRegistry> controllers;
	private WebserverHandler webserverHandler;
	
	static {
		if (LogManager.getRootLogger().getAllAppenders() instanceof NullEnumeration) {
			BasicConfigurator.configure();
			LogManager.getRootLogger().setLevel(Level.INFO);
		}
	}
	
	public void runServer() {
		for (Map.Entry<String, ControllerRegistry> entry : controllers.entrySet()) {
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
	
	public HapporContext setControllers(Map<String, ControllerRegistry> controllers) {
		this.controllers = controllers;
		return this;
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

	public void scanControllers(String packageName) {
		List<ControllerRegistry> scanList = getControllersFromPackage(packageName);
		for (ControllerRegistry registry : scanList) {
			String name = "controller#" + controllers.size() + "_" + registry.getClassName();
			controllers.put(name, registry);
		}
	}

	public abstract HttpController getController(Class<? extends HttpController> clazz);
	
	public static List<ControllerRegistry> getControllersFromPackage(String packageName) {
		List<String> list = PackageUtil.getClassName(packageName);
		List<ControllerRegistry> controllerList = new ArrayList<ControllerRegistry>();
		String defaultClassName = null;
		for (String className : list) {
			className = className.substring(className.indexOf(packageName));
			try {
				Class<?> clazz = Class.forName(className);
				if (HttpController.class.isAssignableFrom(clazz)) {
					if (clazz.isAnnotationPresent(Controller.class)) {
						Controller anno = clazz.getAnnotation(Controller.class);
						ControllerRegistry registry = new ControllerRegistry();
						registry.setClassName(className);
						registry.setMethod(anno.method());
						registry.setUriPattern(anno.uriPattern());
						controllerList.add(registry);
					} else if (clazz.isAnnotationPresent(DefaultController.class)) {
						defaultClassName = className;
					}
				}
			} catch (ClassNotFoundException e) {
				logger.error(e);
			}
		}
		if (defaultClassName != null) {
			ControllerRegistry registry = new ControllerRegistry();
			registry.setClassName(defaultClassName);
			controllerList.add(registry);
		}
		return controllerList;
	}

}
