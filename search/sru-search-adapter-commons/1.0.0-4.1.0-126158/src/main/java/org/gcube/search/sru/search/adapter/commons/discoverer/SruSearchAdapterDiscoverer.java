package org.gcube.search.sru.search.adapter.commons.discoverer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.discoverer.exceptions.DiscovererException;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.gcube.search.sru.search.adapter.commons.Constants;
import org.gcube.search.sru.search.adapter.commons.discoverer.exceptions.SruSearchAdapterDiscoverException;
import org.gcube.search.sru.search.adapter.commons.resources.SruSearchAdapterResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SruSearchAdapterDiscoverer extends Discoverer<SruSearchAdapterResource> implements
SruSearchAdapterDiscovererAPI<SruSearchAdapterResource> {

	private static final Logger logger = LoggerFactory
			.getLogger(SruSearchAdapterDiscoverer.class);

	@Inject
	public SruSearchAdapterDiscoverer(RunningInstancesDiscoverer riDiscoverer,
			IResourceHarvester<SruSearchAdapterResource> harvester) {
		super(riDiscoverer, harvester);
	}

	
	
	public Map<String, Set<String>> discoverSruSearchAdapterNodes(String scope,
			String searchsystemEndpoint) throws SruSearchAdapterDiscoverException {
		
		logger.info("calling discoverSruSearchAdapterNodes with parameters. scope : " + scope + ", searchsystemEndpoint : " + searchsystemEndpoint);
		
		Set<String> endpoints = this
				.discoverSruSearchAdapterNodeRunningInstances(scope);
		
		try {
			Map<String, Set<SruSearchAdapterResource>> foundResources = this
					.discoverResources(endpoints, SruSearchAdapterResource.class, scope);
			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}

			Map<String, Set<String>> serviceResources = new HashMap<String, Set<String>>();

			for (Entry<String, Set<SruSearchAdapterResource>> entry : foundResources
					.entrySet()) {
				String endpoint = entry.getKey();
				Set<SruSearchAdapterResource> resources = entry.getValue();

				for (SruSearchAdapterResource resource : resources) {

					if (filterResource(resource, scope, searchsystemEndpoint)) {

						if (!serviceResources.containsKey(endpoint))
							serviceResources.put(endpoint, new HashSet<String>());
	
						serviceResources.get(endpoint)
								.add(resource.getResourceID());
						
					}
				}
			}

			return serviceResources;

		} catch (DiscovererException e) {
			throw new SruSearchAdapterDiscoverException(
					"error while discovering sru search adapter resources", e);
		}
	}

	public Set<SruSearchAdapterResource> discoverSruSearchAdapterNodeResources(
			String scope, String searchsystemEndpoint)
			throws SruSearchAdapterDiscoverException {
		
		logger.info("calling discoverSruSearchAdapterNodeResources with parameters. scope : " + scope + ", searchsystemEndpoint : " + searchsystemEndpoint);
		
		Set<String> endpoints = this
				.discoverSruSearchAdapterNodeRunningInstances(scope);
		try {
			Map<String, Set<SruSearchAdapterResource>> foundResources = this
					.discoverResources(endpoints, SruSearchAdapterResource.class, scope);

			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}

			Set<SruSearchAdapterResource> serviceResources = new HashSet<SruSearchAdapterResource>();

			for (Entry<String, Set<SruSearchAdapterResource>> entry : foundResources
					.entrySet()) {

				Set<SruSearchAdapterResource> resources = entry.getValue();

				for (SruSearchAdapterResource resource : resources) {

					if (filterResource(resource, scope, searchsystemEndpoint)){
						serviceResources.add(resource);
					}
				}
			}

			return serviceResources;

		} catch (DiscovererException e) {
			throw new SruSearchAdapterDiscoverException(
					"error while discovering sru db resources", e);
		}
	}

	public Set<String> discoverSruSearchAdapterNodeRunningInstances(String scope) {
		return this.discoverRunningInstances(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, Constants.ENDPOINT_KEY, scope);
	}
	
	
	
	private static Boolean filterResource(SruSearchAdapterResource resource, String scope, String searchsystemEndpoint){
		
		logger.info("calling filterResource with parameters : scope : " + scope + ", searchsystemEndpoint : " + searchsystemEndpoint);
		logger.info("                      resource params : scope : " + resource.getScope() + ", searchsystemEndpoint : " + resource.getSearchSystemEndpoint());
		
		if (searchsystemEndpoint != null){
			if (resource.getSearchSystemEndpoint() == null || !resource.getSearchSystemEndpoint().equalsIgnoreCase(searchsystemEndpoint)){
				logger.info("resource databaseName different than : "
						+ searchsystemEndpoint + ". resource has : " + resource.getSearchSystemEndpoint());
				return false;
			}
		}
		
		
		if (scope != null){
			if (resource.getScope() == null || !resource.getScope().equalsIgnoreCase(scope)){
				logger.info("resource scope different than : "
						+ scope + " .resource has : " + resource.getScope());
				return false;
			}
		}
		
		return true;
	}
	
	
}
