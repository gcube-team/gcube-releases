package org.gcube.datapublishing.sdmx.impl.model;

import java.util.HashMap;
import java.util.Map;

import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryDescriptor;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryInterfaceType;

public class SDMXRegistryDescriptorImpl implements SDMXRegistryDescriptor {
	
	Map<SDMXRegistryInterfaceType, String> urls = new HashMap<SDMXRegistryInterfaceType, String>();
	
	public SDMXRegistryDescriptorImpl() {}
	
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
	
}