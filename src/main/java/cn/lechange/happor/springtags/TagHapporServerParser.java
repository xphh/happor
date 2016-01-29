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

import cn.lechange.happor.HapporWebserver;
import cn.lechange.happor.controller.HttpController;
import cn.lechange.happor.utils.PackageUtil;

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
				addController(clazz, method, uriptn);
			}
			Element autoSearch = DomUtils.getChildElementByTagName(controllers, "auto-search");
			if (autoSearch != null) {
				String defaultClazz = packageName + "." + autoSearch.getAttribute("defaultClass");
				List<String> autoList = getControllersFromPackage(packageName);
				for (String clazz : autoList) {
					if (clazzList.contains(clazz)) {
						continue;
					} else if (defaultClazz.equals(clazz)) {
						continue;
					}
					addController(clazz);
				}
				addController(defaultClazz);
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
	
	private void addController(String clazz, String method, String uriPattern) {
		String name = "controller#" + clazzList.size() + "_" + clazz;
		try {
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
					.rootBeanDefinition(Class.forName(clazz));
			BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
			if (!(method == null || method.isEmpty())) {
				beanDefinition.getPropertyValues().addPropertyValue("method", method);
			}
			if (!(uriPattern == null || uriPattern.isEmpty())) {
				beanDefinition.getPropertyValues().addPropertyValue("uriPattern", uriPattern);
			}
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
	
	private void addController(String clazz) {
		addController(clazz, null, null);
	}
	
	private List<String> getControllersFromPackage(String packageName) {
		List<String> list = PackageUtil.getClassName(packageName);
		List<String> controllerList = new ArrayList<String>();
		for (String clazz : list) {
			clazz = clazz.substring(clazz.indexOf(packageName));
			try {
				if (HttpController.class.isAssignableFrom(Class.forName(clazz))) {
					controllerList.add(clazz);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return controllerList;
	}

}
