package cn.lechange.happor;

import java.util.List;

import cn.lechange.happor.controller.HttpController;

public class ControllerContainer {

	private List<HttpController> controllers;

	public List<HttpController> getControllers() {
		return controllers;
	}

	public void setControllers(List<HttpController> controllers) {
		this.controllers = controllers;
	}
	
}
