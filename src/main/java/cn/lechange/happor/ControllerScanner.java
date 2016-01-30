package cn.lechange.happor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.lechange.happor.annotation.Controller;
import cn.lechange.happor.annotation.DefaultController;
import cn.lechange.happor.annotation.Filter;
import cn.lechange.happor.controller.HttpController;
import cn.lechange.happor.utils.PackageUtil;

public class ControllerScanner {
	
	private static Logger logger = Logger.getLogger(ControllerScanner.class);
	
	public static void main(String[] args) {
		ControllerScanner cs = new ControllerScanner();
		cs.scan("");
		for (ControllerRegistry r : cs.getHandlers()) {
			System.out.println(r.getClassName());
		}
		for (Map.Entry<String, ControllerRegistry> entry : cs.getFilters().entrySet()) {
			System.out.println(entry.getKey() + " => " + entry.getValue().getClassName());
		}
	}

	private List<ControllerRegistry> handlers = new ArrayList<ControllerRegistry>();
	private Map<String, ControllerRegistry> filters = new HashMap<String, ControllerRegistry>();
	private String defaultHandlers;

	public void scan(String packageName) {
		List<String> list = PackageUtil.getClassName(packageName);
		for (String className : list) {
			try {
				Class<?> clazz = Class.forName(className);
				if (HttpController.class.isAssignableFrom(clazz)) {
					if (clazz.isAnnotationPresent(Controller.class)) {
						Controller anno = clazz.getAnnotation(Controller.class);
						ControllerRegistry registry = new ControllerRegistry();
						registry.setClassName(className);
						registry.setMethod(anno.method());
						registry.setUriPattern(anno.uriPattern());
						if (clazz.isAnnotationPresent(Filter.class)) {
							Filter annoFilter = clazz.getAnnotation(Filter.class);
							filters.put(annoFilter.value(), registry);
						} else {
							handlers.add(registry);
						}
					} else if (clazz.isAnnotationPresent(DefaultController.class)) {
						defaultHandlers = className;
					}
				}
			} catch (ClassNotFoundException e) {
				logger.error(e);
			}
		}
		if (defaultHandlers != null) {
			ControllerRegistry registry = new ControllerRegistry();
			registry.setClassName(defaultHandlers);
			handlers.add(registry);
		}
	}
	
	public List<ControllerRegistry> getHandlers() {
		return handlers;
	}
	
	public Map<String, ControllerRegistry> getFilters() {
		return filters;
	}
	
	public ControllerRegistry getFilter(String name) {
		return filters.get(name);
	}
	
}
