package org.gcube.search.sru.consumer.common.discoverer;

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
import org.gcube.search.sru.consumer.common.Constants;
import org.gcube.search.sru.consumer.common.discoverer.exceptions.SruConsumerDiscovererException;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SruConsumerDiscoverer extends Discoverer<SruConsumerResource> implements SruConsumerDiscovererAPI<SruConsumerResource> {

	@Inject
	public SruConsumerDiscoverer(RunningInstancesDiscoverer riDiscoverer,
			IResourceHarvester<SruConsumerResource> harvester) {
		super(riDiscoverer, harvester);
		// TODO Auto-generated constructor stub
	}

	private static final Logger logger = LoggerFactory
			.getLogger(SruConsumerDiscoverer.class);

	public Map<String, Set<String>> discoverSruConsumerNodes(String scope, String collectionID)
			throws SruConsumerDiscovererException {
		Set<String> endpoints = this
				.discoverSruConsumerNodeRunningInstances(scope);
		
		try {
			Map<String, Set<SruConsumerResource>> foundResources = this
					.discoverResources(endpoints, SruConsumerResource.class, scope);
			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}

			Map<String, Set<String>> serviceResources = new HashMap<String, Set<String>>();

			for (Entry<String, Set<SruConsumerResource>> entry : foundResources
					.entrySet()) {
				String endpoint = entry.getKey();
				Set<SruConsumerResource> resources = entry.getValue();

				for (SruConsumerResource resource : resources) {

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
			throw new SruConsumerDiscovererException(
					"error while discovering sru consumer resources", e);
		}
	}

	public Set<SruConsumerResource> discoverSruConsumerNodeResources(
			String scope, String collectionID) throws SruConsumerDiscovererException {
		
		
		logger.info("calling discoverSruConsumerNodeResources with parameters. scope : " + scope);
		
		Set<String> endpoints = this
				.discoverSruConsumerNodeRunningInstances(scope);
		
		try {
			Map<String, Set<SruConsumerResource>> foundResources = this
					.discoverResources(endpoints, SruConsumerResource.class, scope);

			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}

			Set<SruConsumerResource> serviceResources = new HashSet<SruConsumerResource>();

			for (Entry<String, Set<SruConsumerResource>> entry : foundResources
					.entrySet()) {

				Set<SruConsumerResource> resources = entry.getValue();

				for (SruConsumerResource resource : resources) {

					if (filterResource(resource, scope, collectionID)){
						serviceResources.add(resource);
					}
				}
			}

			return serviceResources;

		} catch (DiscovererException e) {
			throw new SruConsumerDiscovererException(
					"error while discovering sru consumer resources", e);
		}
		
	}

	public Set<String> discoverSruConsumerNodeRunningInstances(String scope) {
		return this.discoverRunningInstances(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, Constants.ENDPOINT_KEY, scope);
	}
	
	
	
	private static Boolean filterResource(SruConsumerResource resource, String scope, String collectionID){
		
		logger.info("calling filterResource with parameters : scope : " + scope);
		logger.info("                      resource params : scope : " + resource.getScope());
		
		if (scope != null){
			if (resource.getScope() == null || !resource.getScope().equalsIgnoreCase(scope)){
				logger.info("resource scope different than : "
						+ scope + " .resource has : " + resource.getScope());
				return false;
			}
		}
		
		if (collectionID != null){
			if (resource.getCollectionID() == null || !resource.getCollectionID().equalsIgnoreCase(collectionID)){
				logger.info("resource collectionID different than : "
						+ collectionID + " .resource has : " + resource.getCollectionID());
				return false;
			}
		}
		
		return true;
	}
}
