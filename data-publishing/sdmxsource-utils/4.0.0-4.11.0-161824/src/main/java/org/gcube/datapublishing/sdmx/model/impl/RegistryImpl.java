package org.gcube.datapublishing.sdmx.model.impl;

import org.gcube.datapublishing.sdmx.model.Registry;
import org.gcube.datapublishing.sdmx.security.model.impl.BasicCredentials;

public class RegistryImpl implements Registry{

	private String endpoint;
	private BasicCredentials credentials;
	
	public RegistryImpl (String endpoint)
	{
		this.endpoint = endpoint;
	}
	
	@Override
	public String getEndpoint() {
		return endpoint;
	}
	
	
	@Override
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
