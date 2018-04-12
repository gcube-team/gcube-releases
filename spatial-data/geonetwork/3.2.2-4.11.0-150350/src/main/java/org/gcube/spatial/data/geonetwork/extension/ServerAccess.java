package org.gcube.spatial.data.geonetwork.extension;

import org.gcube.spatial.data.geonetwork.LoginLevel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerAccess{
	
	public static enum Version{
		TRE,DUE
	}
	
	private String gnServiceURL;
	private Version version;
	private boolean login=false;
	private String password;
	private String user;
	private LoginLevel loggedLevel=null;
	public ServerAccess(String gnServiceURL, Version version) {
		super();
		this.gnServiceURL = gnServiceURL;
		this.version = version;				
	}
	
	
	
}