package cn.lechange.happor;

import java.util.List;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class HapporChannelInitializer extends
		ChannelInitializer<SocketChannel> {

	private static Logger logger = Logger
			.getLogger(HapporChannelInitializer.class);

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

	public ControllerContainer getControllerContainer() {
		return new ControllerContainer();
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// TODO Auto-generated method stub
		ch.pipeline().addLast(new HttpRequestDecoder())
				.addLast(new HttpResponseEncoder())
				.addLast(new HttpObjectAggregator(getMaxHttpSize()));

		List<HttpBaseController> controllers = getControllerContainer().getControllers();
		if (controllers != null) {
			for (HttpBaseController controller : controllers) {
				logger.debug("add controller: " + controller.getMethod() + " "
						+ controller.getUriPattern());
				ch.pipeline().addLast(getGroup(), controller);
			}
		}

		ch.pipeline().addLast(getGroup(), new DefaultHttpHandler());
	}

}
