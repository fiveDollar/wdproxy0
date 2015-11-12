package com.wd.launcher;

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
import org.apache.log4j.PropertyConfigurator;

import com.wd.config.SelfConf;
import com.wd.db.DBUtil;
import com.wd.db.DBUtil2;
import com.wd.proxy.ProxyHost;
import com.wd.proxy.ProxyUtil;

public class Launcher {
	private static Logger logger = Logger.getLogger(Launcher.class);
	private static int INSERT_MAX = 1000;
	private static ServerSocket server;
	private static List<ProxyHost> proxyList = new ArrayList<ProxyHost>();
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static int KILL_TIME = 30;
	private static int timeout = 3000;
	private static int reNum = 0;
	public static String log4jPath = "etc/log4j.properties";
	
	private static int sum = 0;
	
	public static void main(String[] args) {
		if(System.getProperty("basedir") == null) {
			System.setProperty("basedir", "");
		}
		PropertyConfigurator.configure(System.getProperty("basedir") + log4jPath);
		try {
			server = new ServerSocket(SelfConf.port);
			logger.info("proxy server start, listen port " + SelfConf.port);
			while(!Thread.interrupted()) {
				Socket socket = server.accept();
				socket.setSoTimeout(60000);
				String remoteAddress = socket.getInetAddress().getHostAddress();
				logger.info(remoteAddress + " connected.");
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
						if(proxyList.size() >= INSERT_MAX) {
//							sum += proxyList.size();
//							logger.info(sum);
//							proxyList.clear();
							logger.info("begin to check proxy");
							long b = System.currentTimeMillis();
							ExecutorService executor = Executors.newFixedThreadPool(1000);
							final CountDownLatch doneSignal = new CountDownLatch(proxyList.size());
							final List<ProxyHost> updateList = new ArrayList<ProxyHost>();
							final List<ProxyHost> reCheckList = new ArrayList<ProxyHost>();
							for (int i = 0; i < proxyList.size(); i++) {
								final ProxyHost ph = proxyList.get(i);
								final int index = i;
								executor.execute(new Runnable() {
									public void run() {
										long start = System.currentTimeMillis();
										int flag = checkProxy(ph);
										ph.setSpeed(System.currentTimeMillis() - start);
										Calendar cal = Calendar.getInstance();
										String date = format.format(cal.getTime());
										ph.setAnonymous(flag);
										ph.setImportDate(date);
										if(ph.getAnonymous() >= 1 && ph.getAnonymous() <=3) {
											synchronized (updateList) {
												updateList.add(ph);
											}
										} 
										else if (index >= reNum && ph.getAnonymous() == 4){
											synchronized (reCheckList) {
												//reCheckList.add(ph);
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
							long t1 = System.currentTimeMillis();
							if(updateList.size() > 0) {
								while(true) {
									try {
										logger.info("begin to insert proxy");
										logger.info("size:"+updateList.size());
										DBUtil.executeSql(ProxyUtil.getResultSql(proxyList.size(), (e-b), updateList.size()));
										DBUtil.executeSql(ProxyUtil.getInsertSql(updateList));
										//DBUtil2.executeSql(ProxyUtil.getInsertOrUpdateSql(updateList));
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
							long t2 = System.currentTimeMillis();
							reNum = reCheckList.size();
							
							logger.info("check " + proxyList.size() + " records, use time " + (e-b) + "ms, ok is " + updateList.size() + ", recheck num is " + reNum + ", insert time is " + (t2-t1) + "ms");
							proxyList.clear();
							proxyList.addAll(reCheckList);
						}
					}
				} catch(IOException e) {
					logger.info("IO error " + e.getMessage());
				} catch(Exception e) {
					logger.info("unknow error " + e.getMessage());
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
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
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
		String url = "http://szwindoor.com/test/check_proxy.php";
		HttpURLConnection con = null;
		BufferedReader in = null;
		try {
			con = (HttpURLConnection)new URL(url).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getIp(), proxy.getPort())));
//			con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
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
	
	private static int checkProxy2(ProxyHost proxy) {		
		String url = "http://www.baidu.com/search/error.html";
		HttpURLConnection con = null;
		BufferedReader in = null;
		try {
			con = (HttpURLConnection)new URL(url).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getIp(), proxy.getPort())));
			con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
			con.setConnectTimeout(timeout);
			con.setReadTimeout(timeout);
			
			in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			StringBuilder sb = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
			String numStr = sb.toString();
			if(numStr.length()<30000){
				return 5;
			}
			return 1;
		} catch (NumberFormatException e) {
			return 5;
		} catch (IOException e) {
			e.printStackTrace();
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
