package org.gcube.datapublishing.sdmx.model;

import org.gcube.datapublishing.sdmx.security.model.impl.BasicCredentials;

public interface Registry {


	
	public String getEndpoint();
	
	public BasicCredentials getCredentials();
	
	
}
