package org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence;

public class DatabaseConnectionDescriptor {

	public static enum Flavor{
		POSTGRES,MYSQL
	}
	
	private String username;	
	private String url;
	private String password;

	private Flavor flavor=Flavor.POSTGRES;
	

	@Override
	public String toString() {
		return "DatabaseConnectionDescriptor [username=" + username + ", url=" + url + ", password=" + password
				+ ", flavor=" + flavor + "]";
	}

	public DatabaseConnectionDescriptor(String username, String url, String password) {
		super();
		this.username = username;
		this.url = url;
		this.password = password;
	}

	public DatabaseConnectionDescriptor(String username, String url, String password, Flavor flavor) {
		this(username,url,password);
		this.flavor = flavor;
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
	
	public Flavor getFlavor() {
		return flavor;
	}
	
}
