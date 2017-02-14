package org.gcube.data.analysis.dataminermanagercl.server.util;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ServiceCredentials implements Serializable {

	private static final long serialVersionUID = 3560918948310315680L;
	private String username;
	private String scope;
	private String token;

	public ServiceCredentials() {
		super();
	}

	/**
	 * 
	 * @param user
	 * @param scope
	 * @param token
	 */
	public ServiceCredentials(String username, String scope, String token) {
		super();
		this.username = username;
		this.scope = scope;
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "ServiceCredentials [username=" + username + ", scope=" + scope
				+ ", token=" + token + "]";
	}

	

}
