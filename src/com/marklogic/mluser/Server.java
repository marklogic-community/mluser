package com.marklogic.mluser;

import java.io.Serializable;

public class Server implements Serializable {
	private String address;
	private int port;
	private String adminUser;
	private String adminPassword;
	
	public Server(String address, int port, String adminUser,
			String adminPassword) {
		super();
		this.address = address;
		this.port = port;
		this.adminUser = adminUser;
		this.adminPassword = adminPassword;
	}
	
	public String toString() {
		return adminUser + ":" + adminPassword + "@" + address + ":" + port;
	}
	
	public String getAddress() {
		return address;
	}
	public int getPort() {
		return port;
	}
	public String getAdminUser() {
		return adminUser;
	}
	public String getAdminPassword() {
		return adminPassword;
	}
}
