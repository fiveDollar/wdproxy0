package com.wd.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SelfConf {
	private static final Logger logger = Logger.getLogger(SelfConf.class); 
	private static Properties properties = new Properties();
	
	private static final String FILE_PATH = "etc/self.properties";
	private static final String IP_KEY = "ip";
	private static final String PORT_KEY = "port";
	private static final String IP_DIRECTORY = "ipdirectory";
	
	public static String ip;
	public static int port;
	public static String ipDirectory;
	static {
		try {
			properties.load(new FileInputStream(System.getProperty("basedir") + FILE_PATH));
		} catch (IOException e) {
			logger.error("cannot find the self config file(" + FILE_PATH + ")");
		}
		try {
			ip = properties.getProperty(IP_KEY);
			port = Integer.parseInt(properties.getProperty(PORT_KEY));
			ipDirectory = properties.getProperty(IP_DIRECTORY);
		} catch(NumberFormatException e) {
			ip = "localhost";
			ipDirectory="/var/www/html/port_test/";
		}
	}
	
}
