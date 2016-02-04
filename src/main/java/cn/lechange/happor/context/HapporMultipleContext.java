package cn.lechange.happor.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.lechange.happor.ControllerRegistry;
import cn.lechange.happor.HapporContext;
import cn.lechange.happor.HapporWebserver;
import cn.lechange.happor.WebserverHandler;
import cn.lechange.happor.controller.HttpController;

public class HapporMultipleContext extends HapporContext {
	
	private static Logger logger = Logger.getLogger(HapporMultipleContext.class);

	private HapporWebserver server;
	private HapporContext defaultContext;
	private Map<String, HapporContext> pathContexts = new HashMap<String, HapporContext>();
	
	public HapporMultipleContext() {
		server = new HapporWebserver();
		server.setPathContexts(pathContexts);
		setServer(server);
		setWebserverHandler(new WebserverHandler() {

			public void onInit(HapporWebserver server) {
				// TODO Auto-generated method stub
				if (defaultContext != null) {
					WebserverHandler handler = defaultContext.getWebserverHandler();
					if (handler != null) {
						handler.onInit(server);
					}
				}
				for (Map.Entry<String, HapporContext> entry : pathContexts.entrySet()) {
					WebserverHandler handler = entry.getValue().getWebserverHandler();
					if (handler != null) {
						handler.onInit(server);
					}
				}
			}
			
		});
	}
	
	public void setDefault(HapporContext ctx) {
		ctx.setServer(server);
		defaultContext = ctx;
	}
	
	public void addPath(String path, HapporContext ctx) {
		ctx.setServer(server);
		pathContexts.put(path, ctx);
	}
	
	public void delPath(String path) {
		pathContexts.remove(path);
	}
	
	public void clearPath() {
		pathContexts.clear();
	}
	
	@Override
	public void printInfo() {
		logger.info("This is a multiple context: " + this);
		if (defaultContext != null) {
			logger.info("=> default");
			defaultContext.printInfo();
		}
		for (Map.Entry<String, HapporContext> entry : pathContexts.entrySet()) {
			logger.info("=> path: " + entry.getKey());
			entry.getValue().printInfo();
		}
	}

	@Override
	public Map<String, ControllerRegistry> getControllers() {
		if (defaultContext == null) {
			return null;
		}
		return defaultContext.getControllers();
	}

	@Override
	public HttpController getController(Class<? extends HttpController> clazz) {
		if (defaultContext == null) {
			return null;
		}
		return defaultContext.getController(clazz);
	}

}
