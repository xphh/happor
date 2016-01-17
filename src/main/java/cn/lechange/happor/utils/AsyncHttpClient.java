package cn.lechange.happor.utils;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

public class AsyncHttpClient {

	private static Logger logger = Logger.getLogger(AsyncHttpClient.class);

	private Bootstrap bootstrap;
	
	private int timeout = 3;
	private int maxHttpSize = 8000;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxHttpSize() {
		return maxHttpSize;
	}

	public void setMaxHttpSize(int maxHttpSize) {
		this.maxHttpSize = maxHttpSize;
	}

	public AsyncHttpClient() {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelInboundHandlerAdapter handler = new SimpleChannelInboundHandler<FullHttpResponse>() {

					@Override
					protected void channelRead0(ChannelHandlerContext ctx,
							FullHttpResponse response) throws Exception {
						// TODO Auto-generated method stub
						Callback cb = ctx.channel().attr(KEY_CB).get();
						FullHttpRequest request = ctx.channel().attr(KEY_REQ).get();
						logger.info("recv http response[" + request.getUri() + "] " + response.getStatus());
						cb.onResponse(response.copy());
						ctx.channel().close();
					}

					@Override
					public void userEventTriggered(ChannelHandlerContext ctx,
							Object evt) throws Exception {
						if (evt instanceof IdleStateEvent) {
							Callback cb = ctx.channel().attr(KEY_CB).get();
							FullHttpRequest request = ctx.channel().attr(KEY_REQ).get();
							logger.warn("http request[" + request.getUri() + "] timeout.");
							cb.onTimeout();
							ctx.channel().close();
						}
					}
				};

				ch.pipeline()
						.addLast(new HttpResponseDecoder())
						.addLast(new HttpRequestEncoder())
						.addLast(new HttpObjectAggregator(getMaxHttpSize()))
						.addLast(new IdleStateHandler(getTimeout(), getTimeout(), getTimeout(), TimeUnit.SECONDS))
						.addLast(handler);
			}
		});
	}

	public static interface Callback {
		public void onResponse(FullHttpResponse response);
		public void onTimeout();
		public void onConnectFail();
	}

	final static AttributeKey<Callback> KEY_CB = AttributeKey.valueOf("cb");
	final static AttributeKey<FullHttpRequest> KEY_REQ = AttributeKey.valueOf("req");

	public void sendRequest(final String host, final int port,
			final FullHttpRequest request, final Callback cb) {
		logger.info("send http request[" + request.getUri() + "] to " + host + ":" + port);

		bootstrap.connect(host, port).addListener(new ChannelFutureListener() {

			public void operationComplete(ChannelFuture f) throws Exception {
				// TODO Auto-generated method stub
				if (f.isSuccess()) {
					request.headers().set("Host", host + ":" + port);
					f.channel().attr(KEY_CB).set(cb);
					f.channel().attr(KEY_REQ).set(request);
					f.channel().writeAndFlush(request);
				} else {
					logger.warn("connect to " + host + ":" + port + " failed.");
					cb.onConnectFail();
				}
			}

		});
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("conf/log4j.properties");

		AsyncHttpClient client = new AsyncHttpClient();
		DefaultFullHttpRequest request = new DefaultFullHttpRequest(
				HttpVersion.HTTP_1_1, HttpMethod.GET, "/AsyncHttpClient");
		client.sendRequest("127.0.0.1", 8888, request, new Callback() {

			public void onResponse(FullHttpResponse response) {
				// TODO Auto-generated method stub
				System.out.println("AsyncHttpClient onResponse");
			}

			public void onTimeout() {
				// TODO Auto-generated method stub
				System.out.println("AsyncHttpClient onTimeout");
			}

			public void onConnectFail() {
				// TODO Auto-generated method stub
				System.out.println("AsyncHttpClient onConnectFail");
			}

		});
	}

}
