package org.gcube.common.authorization.library.utils;

import org.gcube.common.authorization.library.provider.ClientInfo;

public class Caller {

	private ClientInfo client;
	private String qualifier;
	
	public Caller(ClientInfo client, String qualifier) {
		super();
		this.client = client;
		this.qualifier = qualifier;
	}
	
	public ClientInfo getClient() {
		return client;
	}
	
	public String getTokenQualifier() {
		return qualifier;
	}


}
