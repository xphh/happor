package cn.lechange.happor;

import cn.lechange.happor.controller.HttpController;

public class ControllerRegistry {

	private String method;
	private String uriPattern;
	private String className;
	private Class<? extends HttpController> clazz;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUriPattern() {
		return uriPattern;
	}

	public void setUriPattern(String uriPattern) {
		this.uriPattern = uriPattern;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setClazz(Class<? extends HttpController> clazz) {
		this.clazz = clazz;
		this.className = clazz.getName();
	}

	public Class<? extends HttpController> getClazz() {
		return clazz;
	}

}
