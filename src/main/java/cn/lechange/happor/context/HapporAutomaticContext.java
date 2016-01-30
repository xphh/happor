package cn.lechange.happor.context;

import org.apache.log4j.Logger;

import cn.lechange.happor.ControllerRegistry;
import cn.lechange.happor.ControllerScanner;

public class HapporAutomaticContext extends HapporManualContext {
	
	private static Logger logger = Logger.getLogger(HapporAutomaticContext.class);

	private ControllerScanner controllerScanner;
	
	public HapporAutomaticContext() {
		this("");
	}

	public HapporAutomaticContext(String packageName) {
		controllerScanner = new ControllerScanner();
		controllerScanner.scan(packageName);
		for (ControllerRegistry r : controllerScanner.getHandlers()) {
			addController(r);
		}
	}
	
	public void addFilters(String[] names) {
		clearControllers();
		for (String name : names) {
			ControllerRegistry r = controllerScanner.getFilter(name);
			if (r == null) {
				logger.error("no filter named '" + name + "'");
			} else {
				addController(r);
			}
		}
		for (ControllerRegistry r : controllerScanner.getHandlers()) {
			addController(r);
		}
	}
	
}
