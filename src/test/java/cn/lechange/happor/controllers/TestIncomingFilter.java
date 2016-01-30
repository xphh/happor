package cn.lechange.happor.controllers;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import cn.lechange.happor.annotation.Controller;
import cn.lechange.happor.annotation.Filter;
import cn.lechange.happor.controller.HttpNormalFilter;

@Controller
@Filter("incoming")
public class TestIncomingFilter extends HttpNormalFilter {

	@Override
	protected void incoming(FullHttpRequest request) {
		// TODO Auto-generated method stub
		System.out.println("incoming...");
		request.headers().set("x-filter-incoming", getClass().getSimpleName());
	}

	@Override
	protected void outgoing(FullHttpResponse response) {
		// TODO Auto-generated method stub
		System.out.println("outgoing...");
		response.headers().set("x-filter-outgoing", getClass().getSimpleName());
	}

}
