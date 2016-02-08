package cn.lechange.happor.springtags;

import java.util.List;
import java.util.Map;

import cn.lechange.happor.ControllerRegistry;
import cn.lechange.happor.HapporWebserver;

public class HapporServerElement {
	
	private HapporWebserver server;
	private String handlerClass;
	private List<ControllerRegistry> controllers;
	private String autoScanPackage;
	private List<String> filters;
	private Map<String, String> configs;
	
	public HapporWebserver getServer() {
		return server;
	}
	public void setServer(HapporWebserver server) {
		this.server = server;
	}
	public String getHandlerClass() {
		return handlerClass;
	}
	public void setHandlerClass(String handlerClass) {
		this.handlerClass = handlerClass;
	}
	public List<ControllerRegistry> getControllers() {
		return controllers;
	}
	public void setControllers(List<ControllerRegistry> controllers) {
		this.controllers = controllers;
	}
	public String getAutoScanPackage() {
		return autoScanPackage;
	}
	public void setAutoScanPackage(String autoScanPackage) {
		this.autoScanPackage = autoScanPackage;
	}
	public List<String> getFilters() {
		return filters;
	}
	public void setFilters(List<String> filters) {
		this.filters = filters;
	}
	public Map<String, String> getConfigs() {
		return configs;
	}
	public void setConfigs(Map<String, String> configs) {
		this.configs = configs;
	}

}
