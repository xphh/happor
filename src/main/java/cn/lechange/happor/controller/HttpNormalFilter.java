package cn.lechange.happor.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpNormalFilter extends HttpController {

	@Override
	protected boolean handleRequest(FullHttpRequest request,
			FullHttpResponse response) {
		// TODO Auto-generated method stub
		incoming(request);
		return false;
	}

	@Override
	protected void handleResponse(FullHttpResponse response) {
		// TODO Auto-generated method stub
		outgoing(response);
	}

	protected abstract void incoming(FullHttpRequest request);
	protected abstract void outgoing(FullHttpResponse response);

}
