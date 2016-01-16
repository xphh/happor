package cn.lechange.happor.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpNormalHandler extends HttpController {

	@Override
	protected boolean handleRequest(FullHttpRequest request,
			FullHttpResponse response) {
		// TODO Auto-generated method stub
		handle(request, response);
		finish(response);
		atlast();
		return true;
	}

	protected abstract void handle(FullHttpRequest request,
			FullHttpResponse response);

	protected abstract void atlast();

}
