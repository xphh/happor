package cn.lechange.happor.context;

import org.apache.log4j.Logger;

import cn.lechange.happor.HapporContext;
import cn.lechange.happor.HapporWebserver;
import cn.lechange.happor.controller.HttpController;

public class HapporManualContext extends HapporContext {

	private static Logger logger = Logger.getLogger(HapporManualContext.class);

	public HapporManualContext() {
		setServer(new HapporWebserver());
	}

	@Override
	public HttpController getController(Class<? extends HttpController> clazz) {
		// TODO Auto-generated method stub
		HttpController controller = null;
		try {
			controller = clazz.newInstance();
		} catch (InstantiationException e) {
			logger.error(e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		}
		return controller;
	}

}
