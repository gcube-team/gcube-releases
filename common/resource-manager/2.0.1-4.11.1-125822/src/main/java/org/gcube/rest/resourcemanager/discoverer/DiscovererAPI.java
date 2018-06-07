package org.gcube.rest.resourcemanager.discoverer;

import java.util.Map;
import java.util.Set;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.resourcemanager.discoverer.exceptions.DiscovererException;

public interface DiscovererAPI<T extends StatefulResource> {

	public Set<String> discoverRunningInstances(String serviceClass, String serviceName, String endpointKey, String scope);
	
	public Map<String, Set<T>> discoverResources(Set<String> endpoints, Class<T> clazz, String scope)
			throws DiscovererException;
	
	public Map<String, Set<String>> discoverResourceIDs(Set<String> endpoints, Class<T> clazz, String scope)
			throws DiscovererException;
}
