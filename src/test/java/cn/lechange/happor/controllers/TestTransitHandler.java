package cn.lechange.happor.controllers;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import cn.lechange.happor.controller.HttpTransitHandler;

public class TestTransitHandler extends HttpTransitHandler {

	@Override
	protected void incoming(FullHttpRequest request) {
		// TODO Auto-generated method stub
		request.setUri("/test/fromtrans");
		request.headers().add("x-incoming", "1");
		transit("127.0.0.1", 9080);
	}

	@Override
	protected void outgoing(FullHttpResponse response) {
		// TODO Auto-generated method stub
		response.headers().add("x-outgoing", "1");
		response.content().writeBytes(" - transit back".getBytes());
		response.headers().set("Content-Length", response.content().readableBytes());
	}

}
