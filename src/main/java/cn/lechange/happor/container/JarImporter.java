package cn.lechange.happor.container;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.log4j.Logger;

import cn.lechange.happor.HapporContext;
import cn.lechange.happor.utils.PackageUtil;

public class JarImporter {

	private static Logger logger = Logger.getLogger(JarImporter.class);

	public static interface JarStub {
		public HapporContext init(ClassLoader classLoader);
	}

	public static HapporContext load(String filename) {
		HapporContext jarContext = null;
		File file = new File(filename);
		URL[] urls = new URL[1];
		try {
			urls[0] = file.toURI().toURL();
		} catch (MalformedURLException e) {
			logger.error(e);
			return null;
		}
		URLClassLoader classLoader = new URLClassLoader(urls);
		List<String> list = PackageUtil.getClassName(classLoader, "!");
		for (String name : list) {
			try {
				Class<?> clazz = classLoader.loadClass(name);
				if (JarStub.class.isAssignableFrom(clazz)) {
					logger.info("JarStub: " + name);
					JarStub stub = (JarStub) clazz.newInstance();
					jarContext = stub.init(classLoader);
					if (jarContext == null) {
						logger.error("cannot get jar context!");
						return null;
					}
				}
			} catch (Exception e) {
				logger.error(e);
				return null;
			}
		}
		return jarContext;
	}

}
