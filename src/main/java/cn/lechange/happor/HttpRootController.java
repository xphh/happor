package cn.lechange.happor;

import java.util.Map;

import org.apache.log4j.Logger;

import cn.lechange.happor.controller.HttpController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpRootController extends ChannelInboundHandlerAdapter {

	private static Logger logger = Logger.getLogger(HttpRootController.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		// TODO Auto-generated method stub
		if (msg instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest) msg;
			FullHttpResponse response = new DefaultFullHttpResponse(
					request.getProtocolVersion(), HttpResponseStatus.OK);
			
			Map<String, HttpController> controllers = HapporHelper.getControllers();
			for (Map.Entry<String, HttpController> entry : controllers.entrySet()) {
				String name = entry.getKey();
				HttpController hc = entry.getValue();
				String method = hc.getMethod();
				String uriPattern = hc.getUriPattern();
				
				if ((method == null || request.getMethod().name().equals(method))
						&& request.getUri().matches(uriPattern)) {
					logger.info("HTTP[" + request.getMethod() + " "
							+ request.getUri() + " " + request.getProtocolVersion()
							+ "] => " + name);
					HttpController controller = HapporHelper.getController(name);
					boolean isEnd = controller.input(ctx, request, response);
					if (isEnd) {
						break;
					}
				}
			}
		}
	}

}
