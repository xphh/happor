package cn.lechange.happor.springtags;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class HapporNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		// TODO Auto-generated method stub
		registerBeanDefinitionParser("server", new TagHapporServerParser());
	}

}
