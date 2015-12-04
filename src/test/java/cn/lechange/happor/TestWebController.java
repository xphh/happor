package cn.lechange.happor;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

public class TestWebController extends HttpBaseController {

	@Override
	protected FullHttpResponse handleRequest(FullHttpRequest request) {
		// TODO Auto-generated method stub
		FullHttpResponse response = new DefaultFullHttpResponse(
				request.getProtocolVersion(), HttpResponseStatus.OK,
				Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8));
		response.headers().set("Content-Type", "text/plain");
		response.headers().set("Content-Length", response.content().readableBytes());
		return response;
	}

	@Override
	protected void postHandle() {
		// TODO Auto-generated method stub
		
	}

}
