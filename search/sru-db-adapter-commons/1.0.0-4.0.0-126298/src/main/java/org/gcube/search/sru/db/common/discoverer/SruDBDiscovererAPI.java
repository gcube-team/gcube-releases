package org.gcube.search.sru.db.common.discoverer;

import java.util.Map;
import java.util.Set;

import org.gcube.search.sru.db.common.discoverer.exceptions.SruDBDiscoverException;
import org.gcube.search.sru.db.common.resources.SruDBResource;


public interface SruDBDiscovererAPI<T extends SruDBResource> {

	public Map<String, Set<String>> discoverSruDBNodes(
			String scope, String databaseName) throws SruDBDiscoverException;

	public Set<T> discoverSruDBNodeResources(
			 String scope, String databaseName)
			throws SruDBDiscoverException;
	
	public Set<String> discoverSruDBNodeRunningInstances(String scope);
}
