package com.wd.tst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.wd.file.FileUtil;

public class Tst1 {
	public static void main(String[] args) {
		File file = new File("data/1.txt");
		StringBuilder sb = new StringBuilder("");
		String ret = read(file);
		//System.out.println(ret);
		sb.append(ret);
		String data = sb.toString();
		//data = data.replaceAll("GET", "\\\\r\\\\nGET");
		System.out.println(data);
	}
	
	public static String read(File file) {
		BufferedReader bufferedReader = null;
		StringBuilder sb = new StringBuilder("");
		try {
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt);
//					System.out.println(lineTxt);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}
}
