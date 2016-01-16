package cn.lechange.happor.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpAsyncHandler extends HttpController {

	@Override
	protected boolean handleRequest(FullHttpRequest request,
			FullHttpResponse response) {
		// TODO Auto-generated method stub
		handle(request, response);
		return true;
	}

	protected abstract void handle(FullHttpRequest request,
			FullHttpResponse response);

}
