package com.wd.tst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class TstProxy {
	public static void main(String[] args) {
		int timeout = 5000;
		String url = "http://www.szwindoor.com/test/test_proxy.php";
		HttpURLConnection con = null;
		BufferedReader in = null;
		try {
			con = (HttpURLConnection)new URL(url).openConnection();
			con.setConnectTimeout(timeout);
			con.setReadTimeout(timeout);
			in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			StringBuilder sb = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
			String numStr = sb.toString();
			System.out.println(Integer.parseInt(numStr));
		} catch (NumberFormatException e) {
			//return 5;
		} catch (IOException e) {
			//return 4;
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
