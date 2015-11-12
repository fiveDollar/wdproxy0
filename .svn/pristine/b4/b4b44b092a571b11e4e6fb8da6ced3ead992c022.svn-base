package com.wd.tst;

import java.io.File;

import com.wd.file.FileUtil;

public class TstYD {
	public static void main(String[] args) {
		File file = new File("印度.csv");
		StringBuilder sb = new StringBuilder("");
		String ret = FileUtil.read(file);
		//System.out.println(ret);
		sb.append(ret);
		String data = sb.toString();
		data = data.replaceAll("\r\n", ",");
		System.out.println(data);
	}
}
