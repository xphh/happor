package cn.lechange.happor.context;

import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.lechange.happor.HapporContext;
import cn.lechange.happor.HapporWebserver;
import cn.lechange.happor.controller.HttpController;

public class HapporManualContext extends HapporContext {

	private static Logger logger = Logger.getLogger(HapporManualContext.class);
	
	public HapporManualContext() {
		setServer(new HapporWebserver());
		setControllers(new HashMap<String, HttpController>());
	}

	@Override
	public HttpController getController(String name) {
		// TODO Auto-generated method stub
		HttpController controller = null;
		if (getControllers().containsKey(name)) {
			try {
				controller = getControllers().get(name).getClass().newInstance();
			} catch (InstantiationException e) {
				logger.error(e.getMessage());
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage());
			}
		}
		return controller;
	}

}
