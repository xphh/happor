package cn.lechange.happor.controller;

import org.springframework.beans.factory.annotation.Autowired;

import cn.lechange.happor.utils.AsyncHttpClient;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpTransitHandler extends HttpAsyncHandler {
	
	@Autowired
	private AsyncHttpClient httpClient;
	
	@Override
	protected void handle(FullHttpRequest request, FullHttpResponse response) {
		// TODO Auto-generated method stub
		incoming(request);
	}
	
	protected void transit(String host, int port) {
		httpClient.sendRequest(host, port, request.copy(), new AsyncHttpClient.Callback() {
			
			public void onConnectFail() {
				// TODO Auto-generated method stub
				finish(null);
			}
			
			public void onTimeout() {
				// TODO Auto-generated method stub
				finish(null);
			}
			
			public void onResponse(FullHttpResponse response) {
				// TODO Auto-generated method stub
				outgoing(response);
				finish(response);
			}

		});
	}
	
	protected abstract void incoming(FullHttpRequest request);
	protected abstract void outgoing(FullHttpResponse response);

}
