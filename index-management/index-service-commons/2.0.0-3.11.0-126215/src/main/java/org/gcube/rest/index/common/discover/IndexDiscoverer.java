package org.gcube.rest.index.common.discover;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.discover.exceptions.IndexDiscoverException;
import org.gcube.rest.index.common.resources.IndexResource;
import org.gcube.rest.resourcemanager.discoverer.Discoverer;
import org.gcube.rest.resourcemanager.discoverer.exceptions.DiscovererException;
import org.gcube.rest.resourcemanager.discoverer.ri.RunningInstancesDiscoverer;
import org.gcube.rest.resourcemanager.harvester.IResourceHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class IndexDiscoverer extends Discoverer<IndexResource>
		implements IndexDiscovererAPI<IndexResource> {

	private static final Logger logger = LoggerFactory
			.getLogger(IndexDiscoverer.class);
	
	@Inject
	public IndexDiscoverer(RunningInstancesDiscoverer riDiscoverer, IResourceHarvester<IndexResource> harvester) {
		super(riDiscoverer, harvester);
	}
	
	@Override
	public Map<String, Set<String>> discoverFulltextIndexNodes(
			String clusterID, String indexID, String collectionID, String scope)
			throws IndexDiscoverException {
		Set<String> endpoints = this
				.discoverFullTextNodeRunningInstances(scope);

		
		try {
			Map<String, Set<IndexResource>> foundResources = this
					.discoverResources(endpoints, IndexResource.class, scope);
			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}

			Map<String, Set<String>> serviceResources = new HashMap<String, Set<String>>();

			for (Entry<String, Set<IndexResource>> entry : foundResources
					.entrySet()) {
				String endpoint = entry.getKey();
				Set<IndexResource> resources = entry.getValue();

				for (IndexResource resource : resources) {

					if (filterResource(resource, clusterID, indexID, scope, collectionID)) {

						if (!serviceResources.containsKey(endpoint))
							serviceResources.put(endpoint, new HashSet<String>());
	
						serviceResources.get(endpoint)
								.add(resource.getResourceID());
						
					}
				}
			}

			return serviceResources;

		} catch (DiscovererException e) {
			throw new IndexDiscoverException(
					"error while discovering fulltextnode resources", e);
		}
	}

	@Override
	public Set<IndexResource> discoverFulltextIndexNodeResources(
			String clusterID, String indexID, String collectionID, String scope)
			throws IndexDiscoverException {

		logger.info("calling discoverFulltextIndexNodeResources with parameters. clusterID : " + clusterID + ", indexID : " + indexID + ", scope : " + scope);
		
		Set<String> endpoints = this
				.discoverFullTextNodeRunningInstances(scope);
		try {
			Map<String, Set<IndexResource>> foundResources = this
					.discoverResources(endpoints, IndexResource.class, scope);

			if (foundResources == null || foundResources.size() == 0) {
				logger.warn("No resources found by the default discoverer");
				return null;
			}

			Set<IndexResource> serviceResources = new HashSet<IndexResource>();

			for (Entry<String, Set<IndexResource>> entry : foundResources
					.entrySet()) {

				Set<IndexResource> resources = entry.getValue();

				for (IndexResource resource : resources) {

					if (filterResource(resource, clusterID, indexID, scope, collectionID)){
						serviceResources.add(resource);
					}
				}
			}

			return serviceResources;

		} catch (DiscovererException e) {
			throw new IndexDiscoverException(
					"error while discovering fulltextnode resources", e);
		}

	}
	
	private static Boolean filterResource(IndexResource resource, String clusterID, String indexID, String scope, String collectionID){
		
		logger.info("calling filterResource with parameters. clusterID : " + clusterID + ", indexID : " + indexID + ", scope : " + scope + ", collectionID : " + collectionID);
		logger.info("                      resource params : clusterID : " + resource.getClusterID() + ", indexID : " + resource.getIndexID() + ", scope : " + resource.getScope() + ", collectionIDs : " + resource.getCollections());
		
		if (clusterID != null){
			if (resource.getClusterID() == null || !resource.getClusterID().equalsIgnoreCase(clusterID)){
				logger.info("resource clusterID different than : "
						+ clusterID + ". resource has : " + resource.getClusterID());
				return false;
			}
		}
		
		if (indexID != null){
			if (resource.getIndexID() == null || !resource.getIndexID().equalsIgnoreCase(indexID)){
				logger.info("resource indexID different than : "
						+ indexID + ". resource has : " + resource.getIndexID());
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
		
		if (collectionID != null){
			if (resource.getCollections() == null || !resource.getCollections().contains(collectionID)){
				logger.info("resource collections different than : "
						+ collectionID + " .resource has : " + resource.getCollections());
				return false;
			}
		}

		return true;
	}

	@Override
	public Set<String> discoverFullTextNodeRunningInstances(String scope) {
		return this.discoverRunningInstances(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, Constants.ENDPOINT_KEY, scope);
	}
}
