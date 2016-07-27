package org.gcube.rest.opensearch.common.discover;

import java.util.Map;
import java.util.Set;

import org.gcube.rest.opensearch.common.discover.exceptions.OpenSearchDiscovererException;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;


public interface OpenSearchDiscovererAPI <T extends OpenSearchDataSourceResource> {

	
	public Map<String, Set<String>> discoverOpenSearchNodes(
			String collectionID, String scope) throws OpenSearchDiscovererException;

	public Set<T> discoverOpenSearchResources(
			String collectionID, String scope) throws OpenSearchDiscovererException;

	public Set<String> discoverOpenSearchInstances(String scope);

	public Set<OpenSearchDataSourceResource> discoverOpenSearchResourcesLocal(
			String scope, String hostname) throws OpenSearchDiscovererException;
}
