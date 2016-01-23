package cn.lechange.happor;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class HapporChannelInitializer extends
		ChannelInitializer<SocketChannel> {
	
	private HapporWebserver server;

    public HapporChannelInitializer(HapporWebserver server) {
    	this.server = server;
    }

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		int timeout = server.getTimeout();
		// TODO Auto-generated method stub
		ch.pipeline().addLast(new HttpRequestDecoder())
				.addLast(new HttpResponseEncoder())
				.addLast(new HttpObjectAggregator(server.getMaxHttpSize()))
				.addLast(new IdleStateHandler(timeout, timeout, timeout, TimeUnit.SECONDS))
				.addLast(new HttpRootController(server));
	}

}
