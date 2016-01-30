package cn.lechange.happor;

import java.util.Map;
import org.apache.log4j.Logger;

import cn.lechange.happor.controller.HttpController;
import cn.lechange.happor.utils.UriParser;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.timeout.IdleStateEvent;

public class HttpRootController extends ChannelInboundHandlerAdapter {
	
	private static Logger logger = Logger.getLogger(HttpRootController.class);

	private HapporWebserver server;
	
	public HttpRootController(HapporWebserver server) {
		this.server = server;
	}
	
	private FullHttpRequest request;
	private FullHttpResponse response;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		if (msg instanceof FullHttpRequest) {
			request = (FullHttpRequest) msg;
			response = new DefaultFullHttpResponse(
					request.getProtocolVersion(), HttpResponseStatus.OK);
			
			HttpController lastController = null;
			
			Map<String, ControllerRegistry> controllers = server.getContext().getControllers();
			for (Map.Entry<String, ControllerRegistry> entry : controllers.entrySet()) {
				ControllerRegistry registry = entry.getValue();
				String method = registry.getMethod();
				String uriPattern = registry.getUriPattern();
				UriParser uriParser = new UriParser(request.getUri());
				
				if (isMethodMatch(method) && isUriMatch(uriParser, uriPattern)) {
					HttpController controller = server.getContext().getController(registry.getClazz());
					controller.setPrev(lastController);
					controller.setServer(server);
					controller.setUriParser(uriParser);
					boolean isEnd = controller.input(ctx, request, response);
					if (isEnd) {
						break;
					}
					lastController = controller;
				}
			}
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx,
			Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			if (request == null) {
				logger.warn("connection[" + ctx + "] timeout.");
				ctx.channel().close();
			} else {
				logger.warn("handle request[" + request.getUri() + "] timeout.");
				response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
				ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
				request.release();
			}
		}
	}
	
	private boolean isMethodMatch(String method) {
		if (method == null || method.isEmpty()) {
			return true;
		} else if (request.getMethod().name().equals(method)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUriMatch(UriParser uri, String pattern) {
		if (pattern == null || pattern.isEmpty()) {
			return true;
		} else if (uri.matches(pattern)) {
			return true;
		} else {
			return false;
		}
	}
}
