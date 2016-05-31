package org.gcube.search.sru.search.adapter.commons.discoverer;

import java.util.Map;
import java.util.Set;

import org.gcube.search.sru.search.adapter.commons.discoverer.exceptions.SruSearchAdapterDiscoverException;
import org.gcube.search.sru.search.adapter.commons.resources.SruSearchAdapterResource;

public interface SruSearchAdapterDiscovererAPI<T extends SruSearchAdapterResource> {

	public Map<String, Set<String>> discoverSruSearchAdapterNodes(
			String scope, String databaseName) throws SruSearchAdapterDiscoverException;

	public Set<T> discoverSruSearchAdapterNodeResources(
			 String scope, String databaseName)
			throws SruSearchAdapterDiscoverException;
	
	public Set<String> discoverSruSearchAdapterNodeRunningInstances(String scope);
	
}
