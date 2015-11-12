package com.wd.tst;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TstSocket {
	public static void main(String[] args) {
		String host = "locahost";
		int port = 8080;
		try {  
            Socket s = new Socket(host, 8080);  
            OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());  
            StringBuffer sb = new StringBuffer();  
            sb.append("GET /HttpStream/gb2312.jsp HTTP/1.1\r\n");  
            sb.append("Host: localhost:8088\r\n");  
            sb.append("Connection: Keep-Alive\r\n");  
            //注，这是关键的关键，忘了这里让我搞了半个小时。这里一定要一个回车换行，表示消息头完，不然服务器会等待  
            sb.append("\r\n");  
            osw.write(sb.toString());  
            osw.flush();  
  
            //--输出服务器传回的消息的头信息  
            InputStream is = s.getInputStream();  
            String line = null;  
            int contentLength = 0;//服务器发送回来的消息长度  
            // 读取所有服务器发送过来的请求参数头部信息  
            do {  
                line = readLine(is, 0);  
                //如果有Content-Length消息头时取出  
                if (line.startsWith("Content-Length")) {  
                    contentLength = Integer.parseInt(line.split(":")[1].trim());  
                }  
                //打印请求部信息  
                System.out.print(line);  
                //如果遇到了一个单独的回车换行，则表示请求头结束  
            } while (!line.equals("\r\n"));  
  
            //--输消息的体  
            System.out.print(readLine(is, contentLength));  
  
            //关闭流  
            is.close();  
  
        } catch (UnknownHostException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }   
	}
	
	private static String readLine(InputStream is, int contentLe) throws IOException {  
        List<Byte> lineByteList = new ArrayList<Byte>();  
        byte readByte;  
        int total = 0;  
        if (contentLe != 0) {  
            do {  
                readByte = (byte) is.read();  
                lineByteList.add(Byte.valueOf(readByte));  
                total++;  
            } while (total < contentLe);//消息体读还未读完  
        } else {  
            do {  
                readByte = (byte) is.read();  
                lineByteList.add(Byte.valueOf(readByte));  
            } while (readByte != 10);  
        }  
  
        byte[] tmpByteArr = new byte[lineByteList.size()];  
        for (int i = 0; i < lineByteList.size(); i++) {  
            tmpByteArr[i] = ((Byte) lineByteList.get(i)).byteValue();  
        }  
        lineByteList.clear();  
  
        return new String(tmpByteArr, "utf-8");  
    }  
}
