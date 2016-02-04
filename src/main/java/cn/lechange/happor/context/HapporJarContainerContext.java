package cn.lechange.happor.context;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.lechange.happor.HapporContext;
import cn.lechange.happor.utils.PackageUtil;

public class HapporJarContainerContext extends HapporMultipleContext {

	private static Logger logger = Logger
			.getLogger(HapporJarContainerContext.class);
	
	public static interface JarStub {
		public HapporContext getContext();
		public String getPath();
	}

	public boolean load(String dir) {
		File root = new File(dir);
		if (!root.isDirectory()) {
			logger.error("cannot load directory: " + dir);
			return false;
		}
		File[] files = root.listFiles();
		if (files == null) {
			return true;
		}
		List<URL> urls = new ArrayList<URL>();
		for (File file : files) {
			if (!file.isFile()) {
				continue;
			}
			String filename = file.getName();
			if (!filename.endsWith(".jar")) {
				continue;
			}
			logger.info("find " + filename);
			try {
				urls.add(file.toURI().toURL());
			} catch (MalformedURLException e) {
				logger.error(e.getMessage());
			}
		}
		URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]));
		List<String> list = PackageUtil.getClassName(classLoader, "!");
		for (String name : list) {
			try {
				Class<?> clazz = Class.forName(name);
				if (JarStub.class.isAssignableFrom(clazz)) {
					logger.info("JarStub: " + name);
					JarStub stub = (JarStub) clazz.newInstance();
					String path = stub.getPath();
					HapporContext ctx = stub.getContext();
					if (path == null) {
						setDefault(ctx);
					} else {
						addPath(path, ctx);
					}
				}
			} catch (ClassNotFoundException e) {
				
			} catch (InstantiationException e) {
				
			} catch (IllegalAccessException e) {
				
			}
		}
		return true;
	}

}
