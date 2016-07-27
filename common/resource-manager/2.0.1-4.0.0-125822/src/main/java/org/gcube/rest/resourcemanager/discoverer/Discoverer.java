package org.gcube.rest.resourcemanager.discoverer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.resourcemanager.discoverer.exceptions.DiscovererException;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Discoverer<T extends StatefulResource> implements DiscovererAPI<T> {

	private static final Logger logger = LoggerFactory
			.getLogger(Discoverer.class);

	private final RunningInstancesDiscoverer riDiscoverer;
	private final IResourceHarvester<T> harvester;
	
	@Inject
	public Discoverer(RunningInstancesDiscoverer riDiscoverer, IResourceHarvester<T> harvester){
		this.riDiscoverer = riDiscoverer;
		this.harvester = harvester;
	}
	
	@Override
	public Set<String> discoverRunningInstances(String serviceClass, String serviceName, String endpointKey, String scope) {
		Set<String> ris = this.riDiscoverer.discoverRunningInstances(serviceClass, serviceName, endpointKey, scope);
		logger.info("Running Instances Found : " + ris);
		return ris;
	}
	
	@Override
	public Map<String, Set<T>> discoverResources(Set<String> endpoints, Class<T> clazz, String scope) throws DiscovererException {
		if (endpoints == null || endpoints.size() == 0){
			logger.warn("No endpoints were given");
				return null;
		}
		
		logger.info("discovering resourceIDs at endpoints : " + endpoints);
		
		Map<String, Set<T>> foundResources = new HashMap<String, Set<T>>();

		for (String endpoint : endpoints) {
			if (endpoint.endsWith("/"))
				endpoint = endpoint.substring(0, endpoint.length() - 1);

			try {
				logger.info("calling harvester...");
				Set<T> resources = this.harvester.getResources(endpoint, clazz, scope);
				logger.info("calling harvester...OK");

				foundResources.put(endpoint, resources);

			} catch (Exception e) {
				logger.warn("No resources found for endpoint : " + endpoint + " will skip this endpoint", e);
//				throw new DiscovererException(
//						"No resources found for endpoint : " + endpoint, e);
			}

		}

		return foundResources;
	}
	
	@Override
	public Map<String, Set<String>> discoverResourceIDs(Set<String> endpoints, Class<T> clazz, String scope) throws DiscovererException {
		if (endpoints == null || endpoints.size() == 0){
				logger.warn("No endpoints were given.");
				return null;
		}
		
		logger.info("discovering resourceIDs at endpoints : " + endpoints);
		
		Map<String, Set<T>> foundResources = new HashMap<String, Set<T>>();

		Map<String, Set<String>> serviceResources = new HashMap<String, Set<String>>();

		for (String endpoint : endpoints) {
			if (endpoint.endsWith("/"))
				endpoint = endpoint.substring(0, endpoint.length() - 1);

			try {
				logger.info("calling harvester...");
				Set<T> resources = this.harvester.getResources(endpoint, clazz, scope);
				logger.info("calling harvester...OK");

				foundResources.put(endpoint, resources);

			} catch (Exception e) {
				logger.warn("No resources found for endpoint : " + endpoint + " will skip endpoint", e);
//				throw new DiscovererException(
//						"No resources found for endpoint : " + endpoint, e);
			}

		}

		for (Entry<String, Set<T>> entry : foundResources.entrySet()) {
			String endpoint = entry.getKey();
			Set<T> resources = entry.getValue();

			for (T resource : resources) {

				if (!serviceResources.containsKey(endpoint))
					serviceResources.put(endpoint, new HashSet<String>());

				serviceResources.get(endpoint).add(resource.getResourceID());
			}
		}

		return serviceResources;

	}
	
}
