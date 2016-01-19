package cn.lechange.happor;

import java.util.Map;

import cn.lechange.happor.controller.HttpController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpRootController extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		if (msg instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest) msg;
			FullHttpResponse response = new DefaultFullHttpResponse(
					request.getProtocolVersion(), HttpResponseStatus.OK);
			
			HttpController lastController = null;
			
			Map<String, HttpController> controllers = HapporHelper.getControllers();
			for (Map.Entry<String, HttpController> entry : controllers.entrySet()) {
				String name = entry.getKey();
				HttpController hc = entry.getValue();
				String method = hc.getMethod();
				String uriPattern = hc.getUriPattern();
				
				if ((method == null || request.getMethod().name().equals(method))
						&& request.getUri().matches(uriPattern)) {
					HttpController controller = HapporHelper.getController(name);
					controller.setPrev(lastController);
					boolean isEnd = controller.input(ctx, request, response);
					if (isEnd) {
						break;
					}
					lastController = controller;
				}
			}
		}
	}

}
