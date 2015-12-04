package cn.lechange.happor;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public abstract class HttpBaseController extends ChannelInboundHandlerAdapter {

	private static Logger logger = Logger.getLogger(HttpBaseController.class);

	private String method;
	private String uriPattern;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		if (msg instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest) msg;
			if ((method == null || request.getMethod().name().equals(method))
					&& request.getUri().matches(uriPattern)) {
				logger.info("HTTP[" + request.getMethod() + " " + request.getUri()
						+ " " + request.getProtocolVersion() + "] => " + getClass().getSimpleName());
				FullHttpResponse response = handleRequest(request);
				if (response != null) {
					ctx.writeAndFlush(response).addListener(
							ChannelFutureListener.CLOSE);
				}
				request.release();
				postHandle();
			} else {
				ctx.fireChannelRead(msg);
			}
		} else {
			ctx.fireChannelRead(msg);
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

	protected abstract FullHttpResponse handleRequest(FullHttpRequest request);
	
	protected abstract void postHandle();
}
