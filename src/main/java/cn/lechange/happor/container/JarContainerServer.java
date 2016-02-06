package cn.lechange.happor.container;

import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cn.lechange.happor.HapporContext;
import cn.lechange.happor.HapporWebserver;
import cn.lechange.happor.context.HapporMultipleContext;
import cn.lechange.happor.springtags.HapporServerElement;

public class JarContainerServer {
	
	private static Logger logger = Logger.getLogger(JarContainerServer.class);

	private HapporWebserver server;
	private String containerConfig;
	
	public JarContainerServer(String filename) {
		FileSystemXmlApplicationContext serverContext = new FileSystemXmlApplicationContext(filename);
		HapporServerElement element = serverContext.getBean(HapporServerElement.class);
		server = element.getServer();
		containerConfig = element.getContainerConfig();
		serverContext.close();
	}
	
	public void load() {
		FileSystemXmlApplicationContext configContext = new FileSystemXmlApplicationContext(containerConfig);
		Map<String, ContainerPath> pathMap = configContext.getBeansOfType(ContainerPath.class);
		configContext.close();
		
		HapporMultipleContext context = new HapporMultipleContext();
		for (Map.Entry<String, ContainerPath> entry : pathMap.entrySet()) {
			String path = entry.getValue().getPath();
			String jar = entry.getValue().getJar();
			logger.info("config path[" +path  + "] " + jar);
			HapporContext ctx = JarImporter.load(jar);
			if (ctx == null) {
				logger.error("load fail: " + jar);
			} else {
				if (path == null) {
					context.setDefault(ctx);
				} else {
					context.addPath(path, ctx);
				}
			}
		}
		context.printInfo();
		context.applyServer(server);
	}

	public void start() {
		Timer timer = new Timer();  
        timer.schedule(checkTask, 1000, 1000);
		server.startup();
	}
	
	private TimerTask checkTask = new TimerTask() {

		private long lastTime = 0;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			File file = new File(containerConfig);
			long nowTime = file.lastModified();
			if (lastTime != nowTime) {
				logger.warn("container config[" + containerConfig + "] changed!");
				load();
				lastTime = nowTime;
			}
		}
		
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JarContainerServer server = new JarContainerServer("conf/containerServer.xml");
		server.start();
	}

}
