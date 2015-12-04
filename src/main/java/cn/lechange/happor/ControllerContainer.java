package cn.lechange.happor;

import java.util.List;

public class ControllerContainer {

	private List<HttpBaseController> controllers;

	public List<HttpBaseController> getControllers() {
		return controllers;
	}

	public void setControllers(List<HttpBaseController> controllers) {
		this.controllers = controllers;
	}
	
}
