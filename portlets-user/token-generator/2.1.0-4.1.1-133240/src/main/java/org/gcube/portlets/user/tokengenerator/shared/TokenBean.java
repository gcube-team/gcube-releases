package org.gcube.portlets.user.tokengenerator.shared;

import java.io.Serializable;

/**
 * This bean contains the user's username, user's token and the current context information
 * @author Costantino Perciante costantino.perciante@isti.cnr.it
 */
@SuppressWarnings("serial")
public class TokenBean implements Serializable {

	private String username;
	private String token;
	private String context;

	public TokenBean() {
		super();
	}

	public TokenBean(String username, String token, String context) {
		super();
		this.username = username;
		this.token = token;
		this.context = context;
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

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return "TokenBean [username=" + username + ", token=" + token.substring(0, 5) + "******************"
				+ ", context=" + context + "]";
	}
}
