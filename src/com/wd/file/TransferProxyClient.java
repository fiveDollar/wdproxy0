package com.wd.file;

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

import com.wd.proxy.ProxyHost;

public class TransferProxyClient {
	private static Logger logger = Logger.getLogger(TransferProxyClient.class);
	public static String log4jPath =  "etc/log4j.properties";
	private static int TRANSFER_MAX = 10;
	
	public static void main(String[] args) {
		if(System.getProperty("basedir") == null) {
			System.setProperty("basedir", "");
		}
		PropertyConfigurator.configure(System.getProperty("basedir") + log4jPath);
		File file = new File("d:/var/www/html/firstRun3128.html");
		BufferedReader bufferedReader = null;
		try {
			if (file.isFile() && file.exists()) {
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
				String lineTxt = null;
				List<ProxyHost> proxyList = new ArrayList<ProxyHost>();
				while (true) {
					lineTxt = bufferedReader.readLine();
					if(lineTxt == null) {//读文件 读不到就休眠1s
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						//add proxy
						String[] attrs = lineTxt.split("[ ]");
						if (attrs.length >= 5) {
							try {
								int port = Integer.parseInt(attrs[2]);
								String host = attrs[3];
								ProxyHost proxy = new ProxyHost(host, port);
								//System.out.println(proxy);
								if(proxy.getPort() <= 65536) {
									proxyList.add(proxy);
								}
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						}
						//transfer proxy
						if(proxyList.size() >= TRANSFER_MAX) {
							//String host = "115.159.2.233";
							String host = "localhost";
							int port = 12468;
							Socket socket = new Socket();
							PrintWriter pw = null;
							try {
								socket.setReuseAddress(true);
								socket.setKeepAlive(true);
								socket.setSoTimeout(5000);
								socket.connect(new InetSocketAddress(host, port), 5000);
								pw = new PrintWriter(socket.getOutputStream());
								for (ProxyHost proxy : proxyList) {
									pw.write(proxy.getIp() + "," + proxy.getPort() + "\r\n");
								}
								pw.flush();
								proxyList.clear();
							} catch (SocketException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								if(pw != null) {
									pw.close();
								}
								if(socket != null) {
									try{
										socket.close();
									} catch(IOException e) {
									}
								}
							}
						}
					}
				}
			} else {
				logger.error("file doesn't exist");
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
	}
}
