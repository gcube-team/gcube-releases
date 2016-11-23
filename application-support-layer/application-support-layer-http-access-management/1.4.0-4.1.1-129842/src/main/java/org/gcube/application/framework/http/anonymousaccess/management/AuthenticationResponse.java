package org.gcube.application.framework.http.anonymousaccess.management;

public class AuthenticationResponse {
	
	private boolean authenticated;
	
	private String errorMessage;
	
	private String userId;
	
	public AuthenticationResponse() {
		authenticated = false;
		errorMessage = new String();
		userId = new String();
	}
	
	public void setAuthenticated(boolean auth) {
		authenticated = auth;
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public void setErrorMessage(String message) {
		errorMessage = message;
	}
	
	public String getUnauthorizedErrorMessage() {
		return errorMessage;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
