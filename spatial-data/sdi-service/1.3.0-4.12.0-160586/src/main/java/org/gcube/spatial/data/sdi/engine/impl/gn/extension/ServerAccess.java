package org.gcube.spatial.data.sdi.engine.impl.gn.extension;

import org.gcube.spatial.data.sdi.model.service.Version;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerAccess{
	
	
	private String gnServiceURL;
	private Version version;

	private String password;
	private String user;

	
	public ServerAccess(String gnServiceURL, Version version) {
		super();
		this.gnServiceURL = gnServiceURL;
		this.version = version;				
	}


	@Override
	public String toString() {
		return "ServerAccess [gnServiceURL=" + gnServiceURL + ", version=" + version + ", password=****" + ", user=" + user + "]";
	}
	
	
	
}