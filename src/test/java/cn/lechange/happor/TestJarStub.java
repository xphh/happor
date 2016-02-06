package cn.lechange.happor;

import cn.lechange.happor.container.JarImporter.JarStub;
import cn.lechange.happor.context.HapporSpringContext;

public class TestJarStub implements JarStub {

	public HapporContext init(ClassLoader classLoader) {
		// TODO Auto-generated method stub
//		HapporContext ctx = new HapporAutomaticContext(classLoader, "!");
//		ctx.setWebserverHandler(new TestWebserverHandler());
		HapporContext ctx = new HapporSpringContext(classLoader, "conf/web.xml");
		return ctx;
	}

}
