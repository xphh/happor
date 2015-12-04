package cn.lechange.happor;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class DefaultHttpHandler extends SimpleChannelInboundHandler<Object> {

	private static Logger logger = Logger.getLogger(DefaultHttpHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		if (msg instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest) msg;
			logger.info("HTTP[" + request.getMethod() + " " + request.getUri()
					+ " " + request.getProtocolVersion() + "]");
			FullHttpResponse response = new DefaultFullHttpResponse(
					request.getProtocolVersion(),
					HttpResponseStatus.BAD_REQUEST);
			ctx.writeAndFlush(response)
					.addListener(ChannelFutureListener.CLOSE);
		} else {
			logger.error("receive error msg, class=" + msg.getClass().getName()
					+ " channel=" + ctx.channel());
			ctx.channel().close();
		}
	}

}
