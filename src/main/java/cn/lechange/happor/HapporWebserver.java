package cn.lechange.happor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

import cn.lechange.happor.utils.AsyncHttpClient;

public class HapporWebserver {

	private static Logger logger = Logger.getLogger(HapporWebserver.class);

	private int port = 80;
	private int executeThreads = 0;
	private int maxHttpSize = 1000000;
	private int timeout = 3;

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getExecuteThreads() {
		return executeThreads;
	}
	public void setExecuteThreads(int executeThreads) {
		this.executeThreads = executeThreads;
	}
	public int getMaxHttpSize() {
		return maxHttpSize;
	}
	public void setMaxHttpSize(int maxHttpSize) {
		this.maxHttpSize = maxHttpSize;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	private HapporContext ctx;
	
	public HapporContext getContext() {
		return ctx;
	}
	
	private AsyncHttpClient asyncHttpClient;

	public AsyncHttpClient getAsyncHttpClient() {
		if (asyncHttpClient == null) {
			asyncHttpClient = new AsyncHttpClient();
			asyncHttpClient.setMaxHttpSize(maxHttpSize);
			asyncHttpClient.setTimeout(timeout);
		}
		return asyncHttpClient;
	}

	public void startup(HapporContext ctx) {
		this.ctx = ctx;
		
		logger.info("HttpServer is starting...");
		logger.info("port = " + port);
		logger.info("timeout = " + timeout);
		logger.info("maxHttpSize = " + maxHttpSize);
		logger.info("executeThreads = " + executeThreads);
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(executeThreads);
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new HapporChannelInitializer(this))
					.option(ChannelOption.SO_BACKLOG, 1024)
					.option(ChannelOption.SO_REUSEADDR, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.TCP_NODELAY, true);

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(port).sync(); // (7)
			logger.info("HttpServer start OK!");
			
			WebserverHandler handler = ctx.getWebserverHandler();
			if (handler != null) {
				handler.onInit();
			}

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully shut down your server.
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("HttpServer start @port[" + port + "] FAIL!");
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}
