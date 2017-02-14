package org.gcube.search.sru.consumer.common.discoverer;

import java.util.Map;
import java.util.Set;

import org.gcube.search.sru.consumer.common.discoverer.exceptions.SruConsumerDiscovererException;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;

public interface SruConsumerDiscovererAPI<T extends SruConsumerResource> {

	public Map<String, Set<String>> discoverSruConsumerNodes(
			String scope, String collectionID) throws SruConsumerDiscovererException;

	public Set<T> discoverSruConsumerNodeResources(
			 String scope, String collectionID)
			throws SruConsumerDiscovererException;
	
	public Set<String> discoverSruConsumerNodeRunningInstances(String scope);
}
