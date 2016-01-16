package cn.lechange.happor;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import cn.lechange.happor.controller.HttpController;

public class TestIncomingFilter extends HttpController {

	@Override
	protected boolean handleRequest(FullHttpRequest request,
			FullHttpResponse response) {
		// TODO Auto-generated method stub
		System.out.println("incoming...");
		return false;
	}

}
