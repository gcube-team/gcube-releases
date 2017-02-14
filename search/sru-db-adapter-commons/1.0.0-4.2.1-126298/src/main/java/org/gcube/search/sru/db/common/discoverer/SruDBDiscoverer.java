package org.gcube.search.sru.db.common.discoverer;

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
import org.gcube.search.sru.db.common.Constants;
import org.gcube.search.sru.db.common.discoverer.exceptions.SruDBDiscoverException;
import org.gcube.search.sru.db.common.resources.SruDBResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SruDBDiscoverer extends Discoverer<SruDBResource> implements
		SruDBDiscovererAPI<SruDBResource> {

	private static final Logger logger = LoggerFactory
			.getLogger(SruDBDiscoverer.class);

	@Inject
	public SruDBDiscoverer(RunningInstancesDiscoverer riDiscoverer,
			IResourceHarvester<SruDBResource> harvester) {
		super(riDiscoverer, harvester);
	}

	@Override
	public Map<String, Set<String>> discoverSruDBNodes(String scope,
			String databaseName) throws SruDBDiscoverException {
		
		Set<String> endpoints = this
				.discoverSruDBNodeRunningInstances(scope);
		
		try {
			Map<String, Set<SruDBResource>> foundResources = this
					.discoverResources(endpoints, SruDBResource.class, scope);
			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}

			Map<String, Set<String>> serviceResources = new HashMap<String, Set<String>>();

			for (Entry<String, Set<SruDBResource>> entry : foundResources
					.entrySet()) {
				String endpoint = entry.getKey();
				Set<SruDBResource> resources = entry.getValue();

				for (SruDBResource resource : resources) {

					if (filterResource(resource, scope, databaseName)) {

						if (!serviceResources.containsKey(endpoint))
							serviceResources.put(endpoint, new HashSet<String>());
	
						serviceResources.get(endpoint)
								.add(resource.getResourceID());
						
					}
				}
			}

			return serviceResources;

		} catch (DiscovererException e) {
			throw new SruDBDiscoverException(
					"error while discovering sru db resources", e);
		}
	}
	
	@Override
	public Set<SruDBResource> discoverSruDBNodeResources(
			String scope, String databaseName) throws SruDBDiscoverException {
		logger.info("calling discoverSruDBNodeResources with parameters. scope : " + scope + ", databaseName : " + databaseName);
		
		Set<String> endpoints = this
				.discoverSruDBNodeRunningInstances(scope);
		try {
			Map<String, Set<SruDBResource>> foundResources = this
					.discoverResources(endpoints, SruDBResource.class, scope);

			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}

			Set<SruDBResource> serviceResources = new HashSet<SruDBResource>();

			for (Entry<String, Set<SruDBResource>> entry : foundResources
					.entrySet()) {

				Set<SruDBResource> resources = entry.getValue();

				for (SruDBResource resource : resources) {

					if (filterResource(resource, scope, databaseName)){
						serviceResources.add(resource);
					}
				}
			}

			return serviceResources;

		} catch (DiscovererException e) {
			throw new SruDBDiscoverException(
					"error while discovering sru db resources", e);
		}
	}
	
	private static Boolean filterResource(SruDBResource resource, String scope, String databaseName){
		
		logger.info("calling filterResource with parameters : scope : " + scope + ", databaseName : " + databaseName);
		logger.info("                      resource params : scope : " + resource.getScope() + ", databaseName : " + resource.getDbName());
		
		if (databaseName != null){
			if (resource.getDbName() == null || !resource.getDbName().equalsIgnoreCase(databaseName)){
				logger.info("resource databaseName different than : "
						+ databaseName + ". resource has : " + resource.getDbName());
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

	@Override
	public Set<String> discoverSruDBNodeRunningInstances(String scope) {
		return this.discoverRunningInstances(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, Constants.ENDPOINT_KEY, scope);
	}

}
