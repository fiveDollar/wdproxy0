package com.wd.proxy;

public class ProxyHost {
	private String ip;
	private int port;
	private int anonymous;
	private String searchAnonymous;
	private String importDate;
	private long speed;
	public ProxyHost() {
	}
	
	public ProxyHost(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(int anonymous) {
		this.anonymous = anonymous;
	}

	public String getImportDate() {
		return importDate;
	}

	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}
	
	public String getSearchAnonymous() {
		return searchAnonymous;
	}

	public void setSearchAnonymous(String searchAnonymous) {
		this.searchAnonymous = searchAnonymous;
	}

	@Override
	public String toString() {
		return "ip:" + ip + ",port:" + port;
	}
}
