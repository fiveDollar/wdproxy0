package com.wd.tst;

import java.io.File;

import com.wd.file.FileUtil;

public class TstMas {
	public static void main(String[] args) {
		File dir = new File("data");
		StringBuilder sb = new StringBuilder("");
		if(dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				String ret = FileUtil.read(file);
				//System.out.println(ret);
				sb.append(ret);
			}
		}
		String data = sb.toString();
		data = data.replaceAll("\r\n", ",");
		System.out.println(data);
	}
}
