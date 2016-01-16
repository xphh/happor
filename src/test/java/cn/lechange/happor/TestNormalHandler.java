package cn.lechange.happor;

import cn.lechange.happor.controller.HttpNormalHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class TestNormalHandler extends HttpNormalHandler {

	@Override
	protected void handle(FullHttpRequest request, FullHttpResponse response) {
		// TODO Auto-generated method stub
		response.content().writeBytes("hello world".getBytes());
		response.headers().set("Content-Type", "text/plain");
		response.headers().set("Content-Length", response.content().readableBytes());
	}

	@Override
	protected void atlast() {
		// TODO Auto-generated method stub
		
	}

}
