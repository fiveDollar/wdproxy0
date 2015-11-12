package com.wd.tst;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import com.wd.file.FileUtil;

public class Tst {
	private static int timeout = 5000;
	public static void main(String[] args) {
		String ret = FileUtil.read(new File("data/test.txt"));
		//System.out.println(ret);
		String[] hosts = ret.split("[\r\n]");
		for (String string : hosts) {
			String[] attrs = string.split("[ ]");
			if(attrs.length >= 5) {
				try {
					int port = Integer.parseInt(attrs[2]);
					String host = attrs[3];
					int flag = checkProxy(host, port);
					System.out.println(host+","+port+","+flag);
				} catch(NumberFormatException e) {
					
				}
			}
		}
	}
	
	private static int checkProxy(String host, int port) {		
		String url = "http://www.szwindoor.com/test/test_proxy.php";
		HttpURLConnection con = null;
		BufferedReader in = null;
		try {
			con = (HttpURLConnection)new URL(url).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));
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
