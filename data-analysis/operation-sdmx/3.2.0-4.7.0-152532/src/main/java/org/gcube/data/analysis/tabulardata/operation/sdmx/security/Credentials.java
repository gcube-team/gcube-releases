package org.gcube.data.analysis.tabulardata.operation.sdmx.security;

public class Credentials {

	private String 	username,
					password;

	Credentials(String userName, String password) {
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
	
	
}
