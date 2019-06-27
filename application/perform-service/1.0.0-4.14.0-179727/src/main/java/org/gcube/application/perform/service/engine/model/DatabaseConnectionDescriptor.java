package org.gcube.application.perform.service.engine.model;

public class DatabaseConnectionDescriptor {

	private String username;	
	private String url;
	private String password;



	@Override
	public String toString() {
		return "DatabaseConnectionDescriptor [username=" + username + ", url=" + url + "]";
	}

	public DatabaseConnectionDescriptor(String username, String url, String password) {
		super();
		this.username = username;
		this.url = url;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getUrl() {
		return url;
	}

	public String getPassword() {
		return password;
	}
	
	
	
}
