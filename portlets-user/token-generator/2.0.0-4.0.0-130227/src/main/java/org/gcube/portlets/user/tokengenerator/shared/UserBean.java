package org.gcube.portlets.user.tokengenerator.shared;

import java.io.Serializable;

/**
 * This bean contains the user's username and token
 * @author Costantino Perciante costantino.perciante@isti.cnr.it
 */
@SuppressWarnings("serial")
public class UserBean implements Serializable {

	private String username;
	private String token;

	public UserBean() {
		super();
	}

	public UserBean(String username, String token) {
		super();
		this.username = username;
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "UserBean [username=" + username + ", token=" + token + "]";
	}
}
