package cn.lechange.happor;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class HapporChannelInitializer extends
		ChannelInitializer<SocketChannel> {

    private EventExecutorGroup group;

	public EventExecutorGroup getGroup() {
		if (group == null) {
			group = new DefaultEventExecutorGroup(executeThreads);
		}
		return group;
	}
	
	private int executeThreads;
	public int getExecuteThreads() {
		return executeThreads;
	}

	public void setExecuteThreads(int executeThreads) {
		this.executeThreads = executeThreads;
	}

	private int maxHttpSize;

	public int getMaxHttpSize() {
		return maxHttpSize;
	}

	public void setMaxHttpSize(int maxHttpSize) {
		this.maxHttpSize = maxHttpSize;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
		ch.pipeline().addLast(new HttpRequestDecoder())
				.addLast(new HttpResponseEncoder())
				.addLast(new HttpObjectAggregator(getMaxHttpSize()))
				.addLast(new HttpRootController());
	}

}
