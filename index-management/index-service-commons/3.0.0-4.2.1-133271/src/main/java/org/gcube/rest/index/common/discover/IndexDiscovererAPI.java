package org.gcube.rest.index.common.discover;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.rest.index.common.discover.exceptions.IndexDiscoverException;

public interface IndexDiscovererAPI {

	public Set<String> discoverFulltextIndexNodes(String scope);
	
//	public Map<String, Set<String>> discoverFulltextIndexNodes(String clusterID, String indexID, String collectionID, String scope) throws IndexDiscoverException;
//
//	public Set<T> discoverFulltextIndexNodeResources(String clusterID, String indexID, String collectionID, String scope) throws IndexDiscoverException;
//	
//	public Set<String> discoverFullTextNodeRunningInstances(String scope);

}
