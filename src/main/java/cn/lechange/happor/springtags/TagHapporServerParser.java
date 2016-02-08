package cn.lechange.happor.springtags;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import cn.lechange.happor.ControllerRegistry;
import cn.lechange.happor.HapporWebserver;

public class TagHapporServerParser extends AbstractSimpleBeanDefinitionParser {

	@Override
	protected String resolveId(Element element,
			AbstractBeanDefinition beanDefinition, ParserContext parserContext) {
		return "webserver";
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
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
		
		HapporWebserver server = new HapporWebserver();
		server.setPort(Integer.valueOf(port));
		server.setTimeout(Integer.valueOf(timeout));
		server.setMaxHttpSize(Integer.valueOf(maxHttpSize));
		server.setExecuteThreads(Integer.valueOf(executeThreads));
		builder.addPropertyValue("server", server);

		Element handler = DomUtils.getChildElementByTagName(element, "handler");
		if (handler != null) {
			String handlerClass = handler.getAttribute("class");
			builder.addPropertyValue("handlerClass", handlerClass);
		}

		List<ControllerRegistry> controllers = new ArrayList<ControllerRegistry>();
		Element controllersTag = DomUtils.getChildElementByTagName(element, "controllers");
		if (controllersTag != null) {
			String packageName = controllersTag.getAttribute("package");
			List<Element> list = DomUtils.getChildElementsByTagName(controllersTag, "controller");
			for (Element controller : list) {
				String className = packageName + "." + controller.getAttribute("class");
				String method = controller.getAttribute("method");
				String uriPattern = controller.getAttribute("uriptn");
				ControllerRegistry registry = new ControllerRegistry();
				registry.setClassName(className);
				registry.setMethod(method);
				registry.setUriPattern(uriPattern);
				controllers.add(registry);
			}
		}
		builder.addPropertyValue("controllers", controllers);

		Element autoScanTag = DomUtils.getChildElementByTagName(element, "controllers-auto-scan");
		if (autoScanTag != null) {
			String packageName = autoScanTag.getAttribute("package");
			builder.addPropertyValue("autoScanPackage", packageName);
			List<String> filters = new ArrayList<String>();
			List<Element> list = DomUtils.getChildElementsByTagName(autoScanTag, "filter");
			for (Element filter : list) {
				String name = filter.getAttribute("name");
				filters.add(name);
			}
			builder.addPropertyValue("filters", filters);
		}
		
		Map<String, String> configs = new HashMap<String, String>();
		List<Element> configTagList = DomUtils.getChildElementsByTagName(element, "config");
		for (Element configTag : configTagList) {
			String type = configTag.getAttribute("type");
			String file = configTag.getAttribute("file");
			configs.put(type, file);
		}
		builder.addPropertyValue("configs", configs);
		
	}

	@Override
	protected Class<?> getBeanClass(Element element) {
		return HapporServerElement.class;
	}
	
}
