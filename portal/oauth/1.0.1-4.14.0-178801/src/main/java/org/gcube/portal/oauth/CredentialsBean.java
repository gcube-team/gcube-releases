package org.gcube.portal.oauth;

public class CredentialsBean {

	private String clientId;
	private String clientSecret;
	
	public CredentialsBean(String clientId, String clientSecret) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public String getClientSecret() {
		return clientSecret;
	}
	
	
}
