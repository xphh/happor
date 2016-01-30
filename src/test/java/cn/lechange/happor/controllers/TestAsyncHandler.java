package cn.lechange.happor.controllers;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import cn.lechange.happor.annotation.Controller;
import cn.lechange.happor.controller.HttpAsyncHandler;

@Controller(method="GET", uriPattern="^/async")
public class TestAsyncHandler extends HttpAsyncHandler {

	@Override
	protected void handle(FullHttpRequest request, final FullHttpResponse response) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				response.content().writeBytes("test async ok!".getBytes());
				response.headers().set("Content-Type", "text/plain");
				response.headers().set("Content-Length", response.content().readableBytes());
				finish(response);
			}
		}.start();
	}

}
