package cn.lechange.happor.springtags;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import cn.lechange.happor.ControllerRegistry;
import cn.lechange.happor.HapporContext;
import cn.lechange.happor.HapporWebserver;

public class TagHapporServerParser extends AbstractSimpleBeanDefinitionParser {

	private ParserContext parserContext;
	private List<String> clazzList = new ArrayList<String>();
	
	@Override
	protected String resolveId(Element element,
			AbstractBeanDefinition beanDefinition, ParserContext parserContext) {
		return "webserver";
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		this.parserContext = parserContext;
		
		String port = element.getAttribute("port");
		String timeout = element.getAttribute("timeout");
		String maxHttpSize = element.getAttribute("maxHttpSize");
		String executeThreads = element.getAttribute("executeThreads");
		String propFile = element.getAttribute("propFile");
		
		if (!propFile.isEmpty()) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(propFile);
				Properties prop = new Properties();
				prop.load(in);
				port = prop.getProperty("happor.server.port", port);
				timeout = prop.getProperty("happor.server.timeout", timeout);
				maxHttpSize = prop.getProperty("happor.server.maxHttpSize", maxHttpSize);
				executeThreads = prop.getProperty("happor.server.executeThreads", executeThreads);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						
					}
				}
			}
		}
		
		builder.addPropertyValue("port", Integer.valueOf(port));
		builder.addPropertyValue("timeout", Integer.valueOf(timeout));
		builder.addPropertyValue("maxHttpSize", Integer.valueOf(maxHttpSize));
		builder.addPropertyValue("executeThreads", Integer.valueOf(executeThreads));

		Element handler = DomUtils.getChildElementByTagName(element, "handler");
		if (handler != null) {
			String clazz = handler.getAttribute("class");
			addBean("handler", clazz);
		}

		Element controllers = DomUtils.getChildElementByTagName(element, "controllers");
		if (controllers != null) {
			String packageName = controllers.getAttribute("package");
			List<Element> list = DomUtils.getChildElementsByTagName(controllers, "controller");
			for (Element controller : list) {
				String clazz = packageName + "." + controller.getAttribute("class");
				String method = controller.getAttribute("method");
				String uriptn = controller.getAttribute("uriptn");
				addRegistry(clazz, method, uriptn);
			}
			Element autoScan = DomUtils.getChildElementByTagName(controllers, "auto-scan");
			if (autoScan != null) {
				List<ControllerRegistry> autoList = HapporContext.getControllersFromPackage(packageName);
				for (ControllerRegistry registry : autoList) {
					addRegistry(registry.getClassName(), registry.getMethod(), registry.getUriPattern());
				}
			}
		}

	}

	@Override
	protected Class<?> getBeanClass(Element element) {
		return HapporWebserver.class;
	}
	
	private void addBean(String name, String clazz) {
		try {
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
					.rootBeanDefinition(Class.forName(clazz));
			BeanDefinitionHolder holder = new BeanDefinitionHolder(
					beanDefinitionBuilder.getBeanDefinition(), name, null);
			registerBeanDefinition(holder, parserContext.getRegistry());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void addRegistry(String clazz, String method, String uriPattern) {
		String name = "controller#" + clazzList.size() + "_" + clazz;
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
				.rootBeanDefinition(ControllerRegistry.class);
		BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		beanDefinition.getPropertyValues().addPropertyValue("className", clazz);
		beanDefinition.getPropertyValues().addPropertyValue("method", method);
		beanDefinition.getPropertyValues().addPropertyValue("uriPattern", uriPattern);
		BeanDefinitionHolder holder = new BeanDefinitionHolder(
				beanDefinition, name, null);
		registerBeanDefinition(holder, parserContext.getRegistry());
		addController(clazz);
	}
	
	private void addController(String clazz) {
		String name = "controllerEntity#" + clazzList.size() + "_" + clazz;
		try {
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
					.rootBeanDefinition(Class.forName(clazz));
			BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
			beanDefinition.setScope("prototype");
			BeanDefinitionHolder holder = new BeanDefinitionHolder(
					beanDefinition, name, null);
			registerBeanDefinition(holder, parserContext.getRegistry());
			clazzList.add(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
