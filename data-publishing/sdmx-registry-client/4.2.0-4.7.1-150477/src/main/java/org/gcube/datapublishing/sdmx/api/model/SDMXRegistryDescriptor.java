package org.gcube.datapublishing.sdmx.api.model;

import org.gcube.datapublishing.sdmx.api.model.security.Credentials;

public interface SDMXRegistryDescriptor {

	public String getUrl(SDMXRegistryInterfaceType interfaceType);
	
	public Credentials getCredentials ();
	
	public boolean versionAware ();

}