package cn.lechange.happor.context;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cn.lechange.happor.ControllerRegistry;
import cn.lechange.happor.ControllerScanner;
import cn.lechange.happor.HapporContext;
import cn.lechange.happor.HapporWebserver;
import cn.lechange.happor.WebserverHandler;
import cn.lechange.happor.controller.HttpController;
import cn.lechange.happor.springtags.HapporServerElement;

public class HapporSpringContext extends HapporContext {

	private static Logger logger = Logger.getLogger(HapporSpringContext.class);

	private FileSystemXmlApplicationContext ctx;
	private ClassLoader classLoader;

	public HapporSpringContext(String filename) {
		this(Thread.currentThread().getContextClassLoader(), filename);
	}

	public HapporSpringContext(ClassLoader classLoader, String filename) {
		this.classLoader = classLoader;
		
		ctx = new FileSystemXmlApplicationContext(filename);
		
		registerAllBeans();

		setServer(ctx.getBean(HapporWebserver.class));

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
	
	@SuppressWarnings("unchecked")
	private void registerAllBeans() {
		HapporServerElement element = ctx.getBean(HapporServerElement.class);
		if (element != null) {
			registerWebserver(element.getServer());
			if (element.getHandlerClass() != null) {
				try {
					Class<WebserverHandler> handlerClass = (Class<WebserverHandler>) classLoader
							.loadClass(element.getHandlerClass());
					registerHandler(handlerClass);
				} catch (ClassNotFoundException e) {
					logger.error(e.getMessage());
				}
			}
			if (element.getFilters() == null) {
				for (ControllerRegistry registry : element.getControllers()) {
					addControllerFromRegistry(registry);
				}
			} else {
				ControllerScanner scanner = new ControllerScanner();
				scanner.scan(classLoader, element.getAutoScanPackage());
				for (String name : element.getFilters()) {
					ControllerRegistry registry = scanner.getFilter(name);
					if (registry == null) {
						logger.error("no filter named '" + name + "'");
					} else {
						addControllerFromRegistry(registry);
					}
				}
				for (ControllerRegistry registry : scanner.getHandlers()) {
					addControllerFromRegistry(registry);
				}
			}
		}
	}
	
	private void addControllerFromRegistry(ControllerRegistry registry) {
		String className = registry.getClassName();
		try {
			@SuppressWarnings("unchecked")
			Class<? extends HttpController> clazz = (Class<? extends HttpController>) classLoader
					.loadClass(className);
			registry.setClazz(clazz);
			registerController(clazz);
			addController(registry);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
	}

	private void registerWebserver(HapporWebserver server) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(HapporWebserver.class);
		builder.addPropertyValue("port", server.getPort());
		builder.addPropertyValue("timeout", server.getTimeout());
		builder.addPropertyValue("maxHttpSize", server.getMaxHttpSize());
		builder.addPropertyValue("executeThreads", server.getExecuteThreads());
		BeanDefinition beanDefinition = builder.getBeanDefinition();
		BeanDefinitionRegistry factory = (BeanDefinitionRegistry) ctx.getBeanFactory();
		factory.registerBeanDefinition("server", beanDefinition);
	}

	private void registerHandler(Class<WebserverHandler> clazz) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
		BeanDefinition beanDefinition = builder.getBeanDefinition();
		BeanDefinitionRegistry factory = (BeanDefinitionRegistry) ctx.getBeanFactory();
		factory.registerBeanDefinition("handler", beanDefinition);
	}
	
	private void registerController(Class<? extends HttpController> clazz) {
		String name = "controller#" + getControllers().size();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
		BeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.setScope("prototype");
		BeanDefinitionRegistry factory = (BeanDefinitionRegistry) ctx.getBeanFactory();
		factory.registerBeanDefinition(name, beanDefinition);
	}

}
