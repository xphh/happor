package cn.lechange.happor;

import cn.lechange.happor.context.HapporSpringContext;
import cn.lechange.happor.context.HapporJarContainerContext.JarStub;

public class TestJarStub implements JarStub {

	public HapporContext getContext() {
		// TODO Auto-generated method stub
		return new HapporSpringContext("conf/web.xml");
	}

	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
