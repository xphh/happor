package cn.lechange.happor.controller;

import org.apache.log4j.Logger;

import cn.lechange.happor.HttpRootController;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpController {

	private static Logger logger = Logger.getLogger(HttpRootController.class);

	private String method;
	private String uriPattern;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUriPattern() {
		return uriPattern;
	}

	public void setUriPattern(String uriPattern) {
		this.uriPattern = uriPattern;
	}
	
	private ChannelHandlerContext ctx;
	private FullHttpRequest request;
	
	final public boolean input(ChannelHandlerContext ctx, FullHttpRequest request,
			FullHttpResponse response) {
		this.ctx = ctx;
		this.request = request;
		return handleRequest(request, response);
	}

	final protected void finish(FullHttpResponse response) {
		if (response != null) {
			logger.info("HTTP[" + request.getMethod() + " " + request.getUri()
					+ " " + request.getProtocolVersion() + "] response " + response.getStatus());
			ctx.channel().writeAndFlush(response)
					.addListener(ChannelFutureListener.CLOSE);
		} else {
			ctx.channel().close();
		}
		request.release();
	}

	protected abstract boolean handleRequest(FullHttpRequest request,
			FullHttpResponse response);

}
