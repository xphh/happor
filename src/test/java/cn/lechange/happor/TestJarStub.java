package cn.lechange.happor;

import cn.lechange.happor.container.JarImporter.JarStub;
import cn.lechange.happor.context.HapporSpringContext;

public class TestJarStub implements JarStub {

	public HapporContext init(ClassLoader classLoader, String jarPath) {
		// TODO Auto-generated method stub
		HapporContext ctx = new HapporSpringContext(classLoader, jarPath + "/conf/web.xml");
		return ctx;
	}

}
