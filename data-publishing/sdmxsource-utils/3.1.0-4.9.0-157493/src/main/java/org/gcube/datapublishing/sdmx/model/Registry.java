package org.gcube.datapublishing.sdmx.model;

import org.gcube.datapublishing.sdmx.security.model.impl.BasicCredentials;

public class Registry {

	private String endpoint;
	private BasicCredentials credentials;
	
	
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public BasicCredentials getCredentials() {
		return credentials;
	}
	public void setCredentials(BasicCredentials credentials) {
		this.credentials = credentials;
	}
	
	@Override
	public String toString() {
		return this.endpoint+ ((this.credentials != null && this.credentials.getUsername() != null) ? " "+this.credentials.getUsername() : "");
	}
	
}
