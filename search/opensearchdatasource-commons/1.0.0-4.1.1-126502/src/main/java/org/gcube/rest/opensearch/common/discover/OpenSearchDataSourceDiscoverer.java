package org.gcube.rest.opensearch.common.discover;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.gcube.rest.opensearch.common.Constants;
import org.gcube.rest.opensearch.common.discover.exceptions.OpenSearchDiscovererException;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;
import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.discoverer.exceptions.DiscovererException;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;


public class OpenSearchDataSourceDiscoverer extends Discoverer<OpenSearchDataSourceResource> implements OpenSearchDiscovererAPI<OpenSearchDataSourceResource> {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenSearchDataSourceDiscoverer.class);
	
	
	@Inject
	public OpenSearchDataSourceDiscoverer(RunningInstancesDiscoverer riDiscoverer, IResourceHarvester<OpenSearchDataSourceResource> harvester) {
		super(riDiscoverer, harvester);
	}
	
	@Override
	public Set<String> discoverOpenSearchInstances(String scope) {
		return this.discoverRunningInstances(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, Constants.ENDPOINT_KEY, scope);
	}
	
	@Override
	public Set<OpenSearchDataSourceResource> discoverOpenSearchResourcesLocal(String scope, String hostname)
			throws OpenSearchDiscovererException {
		
		logger.info("calling discoverFulltextIndexNodeResources with parameters. scope : " + scope);
		
		Set<String> endpoints = this
				.discoverOpenSearchInstances(scope);
		
		String hostEndpoint = null;
		for (String endpoint : endpoints){
			if (endpoint.toLowerCase().startsWith(hostname)){
				hostEndpoint = endpoint;
				break;
			}
		}
		
		if (hostname == null){
			throw new OpenSearchDiscovererException("endpoint for hostname : " + hostname + " not found. all enpoints are : " + endpoints);
		}
		
		try {
			Map<String, Set<OpenSearchDataSourceResource>> foundResources = this
					.discoverResources(Sets.newHashSet(hostEndpoint), OpenSearchDataSourceResource.class, scope);
			
			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}
			
			//should be a map with only one set
			Set<OpenSearchDataSourceResource> hostResources = Sets.newHashSet();
			for (Set<OpenSearchDataSourceResource> resources : foundResources.values()){
				hostResources.addAll(resources);
			}
			
			
			logger.info("found " + " resources in  : " + hostResources.size());
			
			return hostResources;
		} catch (DiscovererException e) {
			throw new OpenSearchDiscovererException(
					"error while discovering fulltextnode resources", e);
		}
	}
	
	@Override
	public Set<OpenSearchDataSourceResource> discoverOpenSearchResources(String collectionID, String scope)
			throws OpenSearchDiscovererException {

		logger.info("calling discoverOpenSearchResources with parameters. collectionID : " + collectionID + ", scope : " + scope);
		
		Set<String> endpoints = this
				.discoverOpenSearchInstances(scope);
		try {
			Map<String, Set<OpenSearchDataSourceResource>> foundResources = this
					.discoverResources(endpoints, OpenSearchDataSourceResource.class, scope);

			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}

			Set<OpenSearchDataSourceResource> serviceResources = new HashSet<OpenSearchDataSourceResource>();

			for (Entry<String, Set<OpenSearchDataSourceResource>> entry : foundResources
					.entrySet()) {

				Set<OpenSearchDataSourceResource> resources = entry.getValue();

				for (OpenSearchDataSourceResource resource : resources) {

					if (filterResource(resource, scope, collectionID)){
						serviceResources.add(resource);
					}
				}
			}

			return serviceResources;

		} catch (DiscovererException e) {
			throw new OpenSearchDiscovererException(
					"error while discovering fulltextnode resources", e);
		}

	}
	
	
	@Override
	public Map<String, Set<String>> discoverOpenSearchNodes(
			String collectionID, String scope)
			throws OpenSearchDiscovererException {
		Set<String> endpoints = this
				.discoverOpenSearchInstances(scope);

		
		try {
			Map<String, Set<OpenSearchDataSourceResource>> foundResources = this
					.discoverResources(endpoints, OpenSearchDataSourceResource.class, scope);
			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}

			Map<String, Set<String>> serviceResources = new HashMap<String, Set<String>>();

			for (Entry<String, Set<OpenSearchDataSourceResource>> entry : foundResources
					.entrySet()) {
				String endpoint = entry.getKey();
				Set<OpenSearchDataSourceResource> resources = entry.getValue();

				for (OpenSearchDataSourceResource resource : resources) {

					if (filterResource(resource, scope, collectionID)) {

						if (!serviceResources.containsKey(endpoint))
							serviceResources.put(endpoint, new HashSet<String>());
	
						serviceResources.get(endpoint)
								.add(resource.getResourceID());
						
					}
				}
			}

			return serviceResources;

		} catch (DiscovererException e) {
			throw new OpenSearchDiscovererException(
					"error while discovering fulltextnode resources", e);
		}
	}
	
	
	private static Boolean filterResource(OpenSearchDataSourceResource resource, String scope, String collectionID){
		
		logger.info("calling filterResource with parameters. scope : " + scope + ", collectionID : " + collectionID);
		logger.info("                      resource params : scope : " + resource.getScope() + ", collectionIDs : " + resource.getCollections());
		
		if (scope != null){
			if (resource.getScope() == null || !resource.getScope().equalsIgnoreCase(scope)){
				logger.info("resource scope different than : "
						+ scope + " .resource has : " + resource.getScope());
				return false;
			}
		}
		
		if (collectionID != null){
			if (resource.getCollections() == null || !resource.getCollections().contains(collectionID)){
				logger.info("resource collections different than : "
						+ collectionID + " .resource has : " + resource.getCollections());
				return false;
			}
		}

		return true;
	}

}
