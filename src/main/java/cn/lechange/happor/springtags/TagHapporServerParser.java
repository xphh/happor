package cn.lechange.happor.springtags;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import cn.lechange.happor.HapporWebserver;

public class TagHapporServerParser extends AbstractSimpleBeanDefinitionParser {

	private static Logger logger = Logger
			.getLogger(TagHapporServerParser.class);
	
	@Override
	protected String resolveId(Element element,
			AbstractBeanDefinition beanDefinition, ParserContext parserContext) {
		return "webserver";
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {
		int port = Integer.valueOf(element.getAttribute("port"));
		int timeout = Integer.valueOf(element.getAttribute("timeout"));
		int maxHttpSize = Integer.valueOf(element.getAttribute("maxHttpSize"));
		int executeThreads = Integer.valueOf(element
				.getAttribute("executeThreads"));

		builder.addPropertyValue("port", port);
		builder.addPropertyValue("timeout", timeout);
		builder.addPropertyValue("maxHttpSize", maxHttpSize);
		builder.addPropertyValue("executeThreads", executeThreads);

		Element handler = DomUtils.getChildElementByTagName(element, "handler");
		if (handler != null) {
			String clazz = handler.getAttribute("class");
			String name = "handler";
			try {
				BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
						.rootBeanDefinition(Class.forName(clazz));
				BeanDefinitionHolder holder = new BeanDefinitionHolder(
						beanDefinitionBuilder.getBeanDefinition(), name, null);
				registerBeanDefinition(holder, parserContext.getRegistry());
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
			}
		}

		Element controllers = DomUtils.getChildElementByTagName(element, "controllers");
		if (controllers != null) {
			String pack = controllers.getAttribute("package");
			List<Element> list = DomUtils.getChildElementsByTagName(controllers, "controller");
			int i = 0;
			for (Element controller : list) {
				String clazz = controller.getAttribute("class");
				if (pack != null) {
					clazz = pack + "." + clazz;
				}
				String method = controller.getAttribute("method");
				String uriptn = controller.getAttribute("uriptn");
				String name = "controller#" + i + "_" + clazz;
				i++;
				try {
					BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
							.rootBeanDefinition(Class.forName(clazz));
					BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
					beanDefinition.getPropertyValues().addPropertyValue("method", method);
					beanDefinition.getPropertyValues().addPropertyValue("uriPattern", uriptn);
					beanDefinition.setScope("prototype");
					BeanDefinitionHolder holder = new BeanDefinitionHolder(
							beanDefinition, name, null);
					registerBeanDefinition(holder, parserContext.getRegistry());
				} catch (ClassNotFoundException e) {
					logger.error(e.getMessage());
				}
			}
		}

	}

	@Override
	protected Class<?> getBeanClass(Element element) {
		return HapporWebserver.class;
	}

}
