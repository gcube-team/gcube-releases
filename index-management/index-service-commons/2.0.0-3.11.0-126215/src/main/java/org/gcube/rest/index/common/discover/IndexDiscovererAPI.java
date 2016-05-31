package org.gcube.rest.index.common.discover;

import java.util.Map;
import java.util.Set;

import org.gcube.rest.index.common.discover.exceptions.IndexDiscoverException;
import org.gcube.rest.index.common.resources.IndexResource;

public interface IndexDiscovererAPI <T extends IndexResource> {

	public Map<String, Set<String>> discoverFulltextIndexNodes(
			String clusterID, String indexID, String collectionID, String scope) throws IndexDiscoverException;

	public Set<T> discoverFulltextIndexNodeResources(
			 String clusterID, String indexID, String collectionID, String scope)
			throws IndexDiscoverException;
	
	public Set<String> discoverFullTextNodeRunningInstances(String scope);

}
