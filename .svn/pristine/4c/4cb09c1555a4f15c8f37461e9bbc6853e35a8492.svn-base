package com.wd.proxy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProxyUtil {
	
	/*public static String getInsertSql(List<ProxyHost> proxyList) {
		StringBuilder sb = new StringBuilder();
		for (ProxyHost proxy : proxyList) {
			String value = "('"+proxy.getIp()+"', "+proxy.getPort()+", "+proxy.getAnonymous()+", '"+proxy.getImportDate()+"')";
			if(sb.length() == 0){
	    		sb.append("insert into check_proxy(`ip`,`port`,`anonymous`,`import_date`) values");
	    	}else{
	    		sb.append(", ");
	    	}
			sb.append(value);
		}
		return sb.toString();
	}*/
	
	public static String getInsertSql(List<ProxyHost> proxyList) {
		StringBuilder sb = new StringBuilder();
		for (ProxyHost proxy : proxyList) {
			proxy.getIp();
			String value = "('"+proxy.getIp()+"', "+proxy.getPort()+", "+proxy.getAnonymous()+",'"+proxy.getSpeed()+"', '"+proxy.getImportDate()+"','"+proxy.getIp().split("\\.")[0]+"','"+proxy.getIp().split("\\.")[1]+"','"+proxy.getIp().split("\\.")[2]+"','"+proxy.getIp().split("\\.")[3]+"', 1)";
			if(sb.length() == 0){
	    		sb.append("insert into check_proxy(`ip`,`port`,`anonymous`,`speed`,`import_date`,`ip_first`,`ip_second`,`ip_third`,`ip_fourth`,`type`) values");
	    	}else{
	    		sb.append(", ");
	    	}
			sb.append(value);
		}
		return sb.toString();
	}
	
	public static String getResultSql(int total, long time, int ok){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(cal.getTime());
		return "insert into check_result(`available`, `check`, `all`, `import_date`) values("+ ok +"," + total + "," + total + ",'" + date + "')"; 
	}
	
	public static List<String> getInsertOrUpdateSql(List<ProxyHost> proxyList) {
		List<String> sqlList = new ArrayList<String>();
		for (ProxyHost proxy : proxyList) {
			String sql = "insert into proxy(`ip`,`port`,`proxy_url_id`,`anonymous`,`search_anonymous`,`import_date`,`day_search`) values('"+proxy.getIp()+"', "+proxy.getPort()+", 23, 1, '"+proxy.getImportDate()+"', '"+proxy.getImportDate()+"', '"+proxy.getImportDate() +"') ON DUPLICATE KEY UPDATE proxy_url_id=23,anonymous=1,search_anonymous='"+proxy.getImportDate()+"',import_date='"+proxy.getImportDate()+"',day_search='"+proxy.getImportDate()+"'";
			sqlList.add(sql);
		}
		return sqlList;		
	}
}