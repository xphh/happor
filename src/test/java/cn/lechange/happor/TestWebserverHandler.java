package cn.lechange.happor;

import org.springframework.beans.factory.annotation.Autowired;

public class TestWebserverHandler implements WebserverHandler {

	@Autowired
	private HapporWebserver server;
	
	public void onInit() {
		// TODO Auto-generated method stub
		System.out.println("TestWebserverHandler [" + server + "] init");
	}

}
