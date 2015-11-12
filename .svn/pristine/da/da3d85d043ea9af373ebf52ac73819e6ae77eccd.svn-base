package com.wd.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.wd.config.SelfConf;
import com.wd.db.DBUtil;
import com.wd.proxy.ProxyHost;
import com.wd.proxy.ProxyUtil;


public class LauncherBak {
	private static Logger logger = Logger.getLogger(LauncherBak.class);
	public static String log4jPath =  "etc/log4j.properties";
	private static int INSERT_MAX = 10000;
	private static int KILL_TIME = 30;
	private static int timeout = 3000;
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static int available = 0;
	private static int check = 0;
	private static int all = 0;
	
	public static void main(String[] args) {
		if(System.getProperty("basedir") == null) {
			System.setProperty("basedir", "");
		}
		File dir = new File(SelfConf.ipDirectory);
		if(dir.isDirectory()){
			File[] files = dir.listFiles();

			for(File ipFile : files){
				check_proxy(ipFile);
			}
		}
		
		
	}
	
	private static void check_proxy(File file){
		if(System.getProperty("basedir") == null) {
			System.setProperty("basedir", "");
		}
		PropertyConfigurator.configure(System.getProperty("basedir") + log4jPath);
		String updateSql = "update check_result set is_used = 1 where id in (select * from  ((select max(id) from check_result)) as t)";
		while(true) {
			try {
				logger.info("begin to update check result");
				DBUtil.executeSql(updateSql);
				logger.info("update check result success");
				break;
			} catch (SQLException se) {
				logger.error("update check result error, do again after 1s");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
			}
		}
		
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					} else {
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
								if(proxyList.size() >= INSERT_MAX) {
									logger.info("begin to check proxy");
									String line = getWC(file);
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
									try {
										all = Integer.parseInt(line);
									} catch(NumberFormatException nfe) {
										logger.error("format line error");
									}
									logger.info("check " + proxyList.size() + " records, use time " + (e-b) + "ms, ok is " + updateList.size() + ", checked is " + check + ", all is " + line);
									proxyList.clear();
									String insertSql = "insert into check_result(`available`,`check`,`all`,`import_date`) values("+available+","+check+","+all+",NOW())";
									while(true) {
										try {
											logger.info("begin to insert check result");
											DBUtil.executeSql(insertSql);
											logger.info("insert check result success");
											break;
										} catch (SQLException se) {
											logger.error("insert check result error, do again after 1s");
										}
										try {
											Thread.sleep(1000);
										} catch (InterruptedException ie) {
										}
									}
								}
							} catch (NumberFormatException e) {
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
	
	private static String getWC(File file){
		DataInputStream din = null;
		Process child = null;
		try {
			child = Runtime.getRuntime().exec("wc -l "+SelfConf.ipDirectory+"/"+file.getName());
			din = new DataInputStream(new BufferedInputStream(child.getInputStream()));
			int c;
			StringBuffer sb = new StringBuffer("");
			byte[] buffer = new byte[1024]; 
			while(true) {
				c = din.read(buffer);
				if(c == -1) {break;}
				sb.append(new String(buffer, 0, c));
			}
			child.waitFor();
			//logger.info("ret:" + sb.toString());
			if (child.exitValue() != 0) {
				return "-1";
			} else {
				String ret = sb.toString();
				int index = ret.indexOf(" ");
				if(index != -1) {
					ret = ret.substring(0, index);
				}
				return ret;
			}
		} catch (IOException e) {
			return "-1";
		} catch (InterruptedException e) {
			return "-1";
		} finally {
			try {
				if(din != null) {
					din.close();
				}
			} catch (IOException e) {
			}
			if(child != null) {
				child.destroy();
			}
		}
	}
}