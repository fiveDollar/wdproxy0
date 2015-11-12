package com.wd.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtil {
	public static String read(File file) {
		BufferedReader bufferedReader = null;
		StringBuilder sb = new StringBuilder("");
		try {
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt+",");
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
