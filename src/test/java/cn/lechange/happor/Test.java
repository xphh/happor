package cn.lechange.happor;

import org.apache.log4j.PropertyConfigurator;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure("conf/log4j.properties");
		HapporHelper.runServer("conf/web.xml");
	}

}
