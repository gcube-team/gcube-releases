package org.gcube.data.access.connector.rest.entity;

public class AccessibleCredentialsEntity {

	private String username;
	private String password;
	private String accessType;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	@Override
	public String toString() {
		return "Credentials [username=" + username + ", accessType=" + accessType + "]";
	}

}
