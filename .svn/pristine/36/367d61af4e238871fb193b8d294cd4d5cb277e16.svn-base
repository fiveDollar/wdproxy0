package com.wd.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.wd.config.SelfConf;
import com.wd.proxy.ProxyHost;

public class Launcher2 {
	private static Logger logger = Logger.getLogger(Launcher2.class);
	public static String log4jPath =  "etc/log4j.properties";
	private static int TRANSFER_MAX = 1000;
	private static Socket socket = null;
	private static PrintWriter pw = null;
	private static int timeout = 3000;
	
	public static void main(String[] args) {
		if(System.getProperty("basedir") == null) {
			System.setProperty("basedir", "");
		}
		PropertyConfigurator.configure(System.getProperty("basedir") + log4jPath);
		
		logger.info("ip:"+ SelfConf.ip+", port:"+ SelfConf.port);
		
		File dir = new File(SelfConf.ipDirectory);
		if(dir.isDirectory()){
			File[] files = dir.listFiles();

			for(File ipFile : files){
				check_proxy(ipFile);
			}
		}
		
	}
	private static void check_proxy(File file){
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String lineTxt = null;
			List<ProxyHost> proxyList = new ArrayList<ProxyHost>();
			while (true && file.exists()) {
				lineTxt = bufferedReader.readLine();
				if(lineTxt == null) {//读文件 读不到就休眠1s
					sleep(1000);	
					logger.info("read nothing, sleep 1s");
				} else {
					//add proxy
					String[] attrs = lineTxt.split("[ ]");
					if (attrs.length >= 5) {
						try {
							int port = Integer.parseInt(attrs[2]);
							String host = attrs[3];
							ProxyHost proxy = new ProxyHost(host, port);
							if(proxy.getPort() <= 65536) {
								proxyList.add(proxy);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
					transferProxys(proxyList);
				}
			}
		} catch (IOException e) {
			logger.error("read file error");
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
				}
			}
		}
		clearSocket();
	}
	
	private static void transferProxys(List<ProxyHost> proxyList) {
		if(proxyList.size() >= TRANSFER_MAX) {
			logger.info("transfer proxy:" + proxyList.size());
			try {
				createConnection();
				for (ProxyHost proxy : proxyList) {
					pw.write(proxy.getIp() + "," + proxy.getPort() + "\r\n");
				}
				pw.flush();
				proxyList.clear();
			} catch (SocketException e) {
				clearSocket();
				logger.error("socket error" + e.getMessage());
			} catch (IOException e) {
				clearSocket();
				logger.error("io error" + e.getMessage());
			} catch (Exception e) {
				clearSocket();
				logger.error("unknow error");
			}
			logger.info("transfer proxy finish");
		}
	}
	
	private static void createSocket() {
		if(socket == null || socket.isClosed() || !socket.isConnected()) {
			socket = new Socket();
			try {
				socket.setReuseAddress(true);
				socket.setKeepAlive(true);
				socket.setSoTimeout(timeout);
			} catch (SocketException e) {
				logger.error("unknow error " + e.getMessage());
			} 
		}
	}
	
	private static void createConnection() throws IOException {
		createSocket();
		if(socket != null && !socket.isConnected()) {
			socket.connect(new InetSocketAddress(SelfConf.ip, SelfConf.port), timeout);
			pw = new PrintWriter(socket.getOutputStream());
		}
	}
	
	private static void clearSocket() {
		if(pw != null) {
			pw.close();
		}
		if(socket == null || socket.isClosed() || !socket.isConnected()) {
			return;
		}
		try {
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (IOException e) {
			logger.error("unknow error " + e.getMessage());
		}
	}
	
	private static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
