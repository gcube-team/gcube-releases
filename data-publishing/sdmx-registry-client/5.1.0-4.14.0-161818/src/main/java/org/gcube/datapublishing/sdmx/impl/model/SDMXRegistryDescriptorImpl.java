package org.gcube.datapublishing.sdmx.impl.model;

import java.util.HashMap;
import java.util.Map;

import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryDescriptor;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryInterfaceType;
import org.gcube.datapublishing.sdmx.security.model.Credentials;
import org.gcube.datapublishing.sdmx.security.model.impl.Base64Credentials;

public class SDMXRegistryDescriptorImpl implements SDMXRegistryDescriptor {
	
	Map<SDMXRegistryInterfaceType, String> urls = new HashMap<SDMXRegistryInterfaceType, String>();

	private Base64Credentials credentials;
	private boolean versionAware;
	
	public SDMXRegistryDescriptorImpl ()
	{
		this.credentials = null;
		this.versionAware = false;
	}
	
	public void setVersionAware (boolean versionAware)
	{
		this.versionAware = versionAware;
	}
	
	public void setUrl(SDMXRegistryInterfaceType interfaceType, String url){
		urls.put(interfaceType, url);
	}

	@Override
	public String getUrl(SDMXRegistryInterfaceType interfaceType) {
		return urls.get(interfaceType);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SDMXRegistryDescriptorImpl [urls=");
		builder.append(urls);
		builder.append("]");
		return builder.toString();
	}

	public void setCredentials (String username, String password)
	{
		if (username != null && password != null) this.credentials = new Base64Credentials(username, password);
	}
	
	
	@Override
	public Credentials getCredentials() {

		return this.credentials;
	}

	@Override
	public boolean versionAware() 
	{
		return this.versionAware;
	}
	
}