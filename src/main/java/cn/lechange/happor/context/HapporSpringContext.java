package cn.lechange.happor.context;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cn.lechange.happor.ControllerRegistry;
import cn.lechange.happor.HapporContext;
import cn.lechange.happor.HapporWebserver;
import cn.lechange.happor.WebserverHandler;
import cn.lechange.happor.controller.HttpController;

public class HapporSpringContext extends HapporContext {

	private static Logger logger = Logger.getLogger(HapporSpringContext.class);

	private FileSystemXmlApplicationContext ctx;

	public HapporSpringContext(String filename) {
		ctx = new FileSystemXmlApplicationContext(filename);
		
		setServer(ctx.getBean(HapporWebserver.class));
		
		Map<String, ControllerRegistry> controllers = ctx.getBeansOfType(ControllerRegistry.class);
		for (Map.Entry<String, ControllerRegistry> entry : controllers.entrySet()) {
			addController(entry.getValue());
		}
		
		try {
			setWebserverHandler(ctx.getBean(WebserverHandler.class));
		} catch (NoSuchBeanDefinitionException e) {
			logger.warn("has no WebserverHandler");
		}
	}
	
	public ApplicationContext getApplicationContext() {
		return ctx;
	}

	public void close() {
		if (ctx != null) {
			ctx.close();
		}
	}

	@Override
	public HttpController getController(Class<? extends HttpController> clazz) {
		// TODO Auto-generated method stub
		return (HttpController) ctx.getBean(clazz);
	}

}
