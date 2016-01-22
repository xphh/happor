package cn.lechange.happor.controller;

import org.apache.log4j.Logger;

import cn.lechange.happor.HapporWebserver;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpController {

	private static Logger logger = Logger.getLogger(HttpController.class);

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
		logger.info("HTTP[" + request.getMethod() + " "
				+ request.getUri() + " " + request.getProtocolVersion()
				+ "] => " + this + " [ from " + prev + " ]");
		this.ctx = ctx;
		this.request = request;
		return handleRequest(request, response);
	}
	
	final public void output(FullHttpResponse response) {
		handleResponse(response);
	}
	
	private HttpController prev;
	final public void setPrev(HttpController controller) {
		prev = controller;
	}
	
	private HapporWebserver server;
	public void setServer(HapporWebserver server) {
		this.server = server;
	}
	public HapporWebserver getServer() {
		return server;
	}
	
	private void realFinish(FullHttpResponse response) {
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

	protected void finish(FullHttpResponse response) {
		handleResponse(response);
		if (prev == null) {
			realFinish(response);
		} else {
			prev.finish(response);
		}
	}

	protected abstract boolean handleRequest(FullHttpRequest request,
			FullHttpResponse response);
	protected abstract void handleResponse(FullHttpResponse response);

}
