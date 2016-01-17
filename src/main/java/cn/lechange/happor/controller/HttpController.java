package cn.lechange.happor.controller;

import org.apache.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class HttpController extends ChannelInboundHandlerAdapter {

	private static Logger logger = Logger.getLogger(HttpController.class);

	private String method;
	private String uriPattern;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		if (msg instanceof FullHttpRequest) {
			channel = ctx.channel();
			request = (FullHttpRequest) msg;
			if ((method == null || request.getMethod().name().equals(method))
					&& request.getUri().matches(uriPattern)) {
				logger.info("HTTP[" + request.getMethod() + " "
						+ request.getUri() + " " + request.getProtocolVersion()
						+ "] => " + getClass().getSimpleName());

				FullHttpResponse response = new DefaultFullHttpResponse(
						request.getProtocolVersion(), HttpResponseStatus.OK);
				boolean isEnd = handleRequest(request, response);
				if (!isEnd) {
					ctx.fireChannelRead(msg);
				}
			} else {
				ctx.fireChannelRead(msg);
			}
		}
	}

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

	protected abstract boolean handleRequest(FullHttpRequest request,
			FullHttpResponse response);

	protected Channel channel;
	protected FullHttpRequest request;

	protected void finish(FullHttpResponse response) {
		if (response != null) {
			logger.info("HTTP[" + request.getMethod() + " " + request.getUri()
					+ " " + request.getProtocolVersion() + "] response " + response.getStatus());
			channel.writeAndFlush(response)
					.addListener(ChannelFutureListener.CLOSE);
		} else {
			channel.close();
		}
		request.release();
	}

}
