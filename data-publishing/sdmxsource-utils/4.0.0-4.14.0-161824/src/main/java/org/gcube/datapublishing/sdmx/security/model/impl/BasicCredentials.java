package org.gcube.datapublishing.sdmx.security.model.impl;

import org.gcube.datapublishing.sdmx.security.model.Credentials;

public class BasicCredentials implements Credentials{

	private String 	username,
					password;

	private final String TYPE = "BASIC";
	
	public BasicCredentials(String userName, String password) {
		this.username = userName;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}

	

	public String getPassword() {

		return password;
	}

	@Override
	public String toString ()
	{
		if (this.username == null) return "Null credentials";
		
		else return "Username: "+username+ " Password: "+password;
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	
}
