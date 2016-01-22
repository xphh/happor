package cn.lechange.happor.controller;

import cn.lechange.happor.utils.AsyncHttpClient;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class HttpTransitHandler extends HttpAsyncHandler {
	
	private FullHttpRequest transRequest;
	private FullHttpResponse transResponse;
	
	@Override
	protected void handle(FullHttpRequest request, FullHttpResponse response) {
		// TODO Auto-generated method stub
		transRequest = request.retain();
		incoming(transRequest);
	}
	
	protected void transit(String host, int port) {
		
		getServer().getAsyncHttpClient().sendRequest(host, port, transRequest, 
				new AsyncHttpClient.Callback() {
			
			public void onConnectFail() {
				// TODO Auto-generated method stub
				transResponse = new DefaultFullHttpResponse(
						transRequest.getProtocolVersion(), new HttpResponseStatus(491, "Transit Connect Fail"));
				finish(transResponse);
			}
			
			public void onTimeout() {
				// TODO Auto-generated method stub
				transResponse = new DefaultFullHttpResponse(
						transRequest.getProtocolVersion(), new HttpResponseStatus(492, "Transit Timeout"));
				finish(transResponse);
			}
			
			public void onResponse(FullHttpResponse response) {
				// TODO Auto-generated method stub
				transResponse = response.retain();
				outgoing(transResponse);
				finish(transResponse);
			}

		});
	}
	
	protected abstract void incoming(FullHttpRequest request);
	protected abstract void outgoing(FullHttpResponse response);

}
