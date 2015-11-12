package com.wd.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.wd.config.SelfConf;
import com.wd.db.DBUtil;
import com.wd.proxy.ProxyHost;
import com.wd.proxy.ProxyUtil;

public class TransferProxyServer {
	private static Logger logger = Logger.getLogger(TransferProxyServer.class);
	private static int INSERT_MAX = 10000;
	private static final int PORT = 12468;
	private static ServerSocket server;
	private static List<ProxyHost> proxyList = new ArrayList<ProxyHost>();
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static int KILL_TIME = 30;
	private static int available = 0;
	private static int check = 0;
	private static int timeout = 3000;
	
	public static void main(String[] args) {
		try {
			server = new ServerSocket(PORT);
			logger.info("proxy server start, listen port " + PORT);
			while(!Thread.interrupted()) {
				Socket socket = server.accept();
				logger.info(socket.getInetAddress().getHostAddress() + " connected.");
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String readLine;
					while(true) {
						readLine = br.readLine();
						if(readLine == null) {
							break;
						}
						String[] attrs = readLine.split("[,]");
						if (attrs.length == 2) {
							try {
								int port = Integer.parseInt(attrs[1]);
								String host = attrs[0];
								ProxyHost proxy = new ProxyHost(host, port);
								//System.out.println(proxy);
								if(proxy.getPort() <= 65536) {
									proxyList.add(proxy);
								}
							} catch(NumberFormatException e) {
								e.printStackTrace();
							}
						}
					}
				} catch(IOException e) {
					e.printStackTrace();
				} finally {
					if(br != null) {
						br.close();
					}
					if(socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							
						}
					}
				}
				
				if(proxyList.size() >= INSERT_MAX) {
					logger.info("begin to check proxy");
					long b = System.currentTimeMillis();
					ExecutorService executor = Executors.newFixedThreadPool(1000);
					final CountDownLatch doneSignal = new CountDownLatch(proxyList.size());
					final List<ProxyHost> updateList = new ArrayList<ProxyHost>();
					for (int i = 0; i < proxyList.size(); i++) {
						final ProxyHost ph = proxyList.get(i);
						executor.execute(new Runnable() {
							public void run() {
								int flag = checkProxy(ph);
								Calendar cal = Calendar.getInstance();
								String date = format.format(cal.getTime());
								ph.setAnonymous(flag);
								ph.setImportDate(date);
								if(ph.getAnonymous() >= 1 && ph.getAnonymous() <=3) {
									synchronized (updateList) {
										updateList.add(ph);
									}
								}	
								doneSignal.countDown();
							}
						});
					}
					try {
						doneSignal.await(KILL_TIME, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						logger.error("check proxy error");
					}
					executor.shutdown();
					logger.info("check proxy success");
					long e = System.currentTimeMillis();
					if(updateList.size() > 0) {
						while(true) {
							try {
								logger.info("begin to insert proxy");
								DBUtil.executeSql(ProxyUtil.getInsertSql(updateList));
								logger.info("insert proxy success");
								break;
							} catch (SQLException se) {
								logger.error("insert proxy error, do again after 1s");
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException ie) {
							}
						}
					}
					available = available + updateList.size();
					check = check + proxyList.size();
					logger.info("check " + proxyList.size() + " records, use time " + (e-b) + "ms, ok is " + updateList.size() + ", checked is " + check);
					proxyList.clear();
				}
			}
		} catch (IOException e) {
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private static int checkProxy(ProxyHost proxy) {
		String url = "http://"+SelfConf.ip+"/check_proxy.php";
		HttpURLConnection con = null;
		BufferedReader in = null;
		try {
			con = (HttpURLConnection)new URL(url).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getIp(), proxy.getPort())));
			con.setConnectTimeout(timeout);
			con.setReadTimeout(timeout);
			in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			StringBuilder sb = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
			String numStr = sb.toString();
			return Integer.parseInt(numStr);
		} catch (NumberFormatException e) {
			return 5;
		} catch (IOException e) {
			return 4;
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
