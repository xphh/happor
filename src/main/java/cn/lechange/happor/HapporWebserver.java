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
import org.springframework.beans.factory.annotation.Autowired;

public class HapporWebserver {

	private static Logger logger = Logger.getLogger(HapporWebserver.class);

	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	@Autowired
	private HapporChannelInitializer channelInitializer;
	
	private HapporContext ctx;
	
	public HapporContext getContext() {
		return ctx;
	}

	public void startup(HapporContext ctx) {
		this.ctx = ctx;
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(channelInitializer)
					.option(ChannelOption.SO_BACKLOG, 1024)
					.option(ChannelOption.SO_REUSEADDR, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childOption(ChannelOption.TCP_NODELAY, true);

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(port).sync(); // (7)
			logger.info("HttpServer start @port[" + port + "] OK!");
			
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
