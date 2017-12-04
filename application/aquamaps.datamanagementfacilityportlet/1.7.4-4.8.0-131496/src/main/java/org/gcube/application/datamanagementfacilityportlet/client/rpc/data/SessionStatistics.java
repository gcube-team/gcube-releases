package org.gcube.application.datamanagementfacilityportlet.client.rpc.data;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SessionStatistics implements IsSerializable {

	private String scope;
	private String userName;
	
	public SessionStatistics() {
		// TODO Auto-generated constructor stub
	}
	
	public SessionStatistics(String scope, String userName) {
		super();
		this.scope = scope;
		this.userName = userName;
	}

	public String getScope() {
		return scope;
	}
	public String getUserName() {
		return userName;
	}
}
