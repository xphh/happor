package cn.lechange.happor.container;

import java.io.File;

public class ServerMain {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JarContainerServer server = new JarContainerServer("conf/containerServer.xml");
		if (args.length == 0) {
			server.start();
		} else if (args.length == 1) {
			String cmd = args[0];
			if (cmd.equals("reload")) {
				System.out.println("Reloading...");
				reload(server.getContainerConfig());
			} else {
				System.err.println("no cmd '" + cmd + "'");
			}
		} else {
			System.err.println("wrong cmd");
		}
	}
	
	public static void reload(String filename) {
		File log = new File(JarContainerServer.LOAD_LOG);
		long lastTime = log.lastModified();

		File file = new File(filename);
		if (!file.exists()) {
			System.err.println("config file[" + filename + "] is not exist!");
			return;
		}
		file.setLastModified(System.currentTimeMillis());
		for (int i = 0; i < 3; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long time = log.lastModified();
			if (time != lastTime) {
				System.out.println("Reloaded.");
				return;
			}
		}
		
		System.err.println("Reload Fail!");
	}

}
