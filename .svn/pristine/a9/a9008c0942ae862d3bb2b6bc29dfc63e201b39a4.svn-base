package com.wd.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DBConf {
	private static final Logger logger = Logger.getLogger(DBConf.class); 
	private static Properties properties = new Properties();
	
	private static final String FILE_PATH = "etc/db.properties";
	private static final String URL_KEY = "url";
	private static final String USER_KEY = "user";
	private static final String PASSWORD_KEY = "password";

	public static String url;
	public static String user;
	public static String password;

	static {
		try {
			properties.load(new FileInputStream(System.getProperty("basedir") + FILE_PATH));
		} catch (IOException e) {
			logger.error("cannot find the db config file(" + FILE_PATH + ")");
		}
		url = properties.getProperty(URL_KEY);
		user = properties.getProperty(USER_KEY);
		password = properties.getProperty(PASSWORD_KEY);
	}
}
