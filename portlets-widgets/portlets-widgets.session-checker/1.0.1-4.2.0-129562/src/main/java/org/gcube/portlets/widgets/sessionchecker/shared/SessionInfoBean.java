package org.gcube.portlets.widgets.sessionchecker.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SessionInfoBean implements Serializable {

	private String username;
	private String scope;
	private boolean isDevMode;
	
	public SessionInfoBean() {
		super();
	}


	public SessionInfoBean(String username, String scope) {
		super();
		this.username = username;
		this.scope = scope;
	}

	

	public SessionInfoBean(String username, String scope, boolean isDevMode) {
		super();
		this.username = username;
		this.scope = scope;
		this.isDevMode = isDevMode;
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


	public boolean isDevMode() {
		return isDevMode;
	}


	public void setDevMode(boolean isDevMode) {
		this.isDevMode = isDevMode;
	}
	
	
}
