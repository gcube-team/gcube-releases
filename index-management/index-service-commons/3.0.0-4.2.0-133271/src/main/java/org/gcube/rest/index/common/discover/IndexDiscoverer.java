package org.gcube.rest.index.common.discover;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.rest.index.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IndexDiscoverer implements IndexDiscovererAPI {

	private static final Logger logger = LoggerFactory.getLogger(IndexDiscoverer.class);
	
	
	@Override
	public Set<String> discoverFulltextIndexNodes(String scope){
		
		ScopeProvider.instance.set(scope);
		
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		
		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+Constants.SERVICE_CLASS+"'")
		     .addCondition("$resource/Profile/ServiceName/text() eq '"+Constants.SERVICE_NAME+"'");
		
		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		
		List<GCoreEndpoint> eprs = client.submit(query);
		
		Set<String> clusterHosts = new HashSet<String>();
		for(GCoreEndpoint epr : eprs){
			if(!epr.scopes().contains(scope))
				continue;
			if(!"ready".equals(epr.profile().deploymentData().status().toLowerCase()))
				continue;
			for(Endpoint e : epr.profile().endpointMap().values().toArray(new Endpoint[epr.profile().endpointMap().values().size()]))
				if(!e.uri().toString().endsWith("/gcube/resource"))
					clusterHosts.add(e.uri().toString());
		}
		
		logger.info("Discovered index cluster nodes: " + Arrays.toString(clusterHosts.toArray()));
		return clusterHosts;
		
	}
	
	
	
//	@Inject
//	public IndexDiscoverer(RunningInstancesDiscoverer riDiscoverer, IResourceHarvester<IndexResource> harvester) {
//		super(riDiscoverer, harvester);
//	}
//	
//	@Override
//	public Map<String, Set<String>> discoverFulltextIndexNodes(String clusterID, String indexID, String collectionID, String scope) throws IndexDiscoverException {
//		Set<String> endpoints = this.discoverFullTextNodeRunningInstances(scope);
//
//		try {
//			Map<String, Set<IndexResource>> foundResources = this.discoverResources(endpoints, IndexResource.class, scope);
//			if (foundResources == null || foundResources.size() == 0) {
//				logger.warn("No resources found by the default discoverer");
//				return null;
//			}
//
//			Map<String, Set<String>> serviceResources = new HashMap<String, Set<String>>();
//
//			for (Entry<String, Set<IndexResource>> entry : foundResources
//					.entrySet()) {
//				String endpoint = entry.getKey();
//				Set<IndexResource> resources = entry.getValue();
//
//				for (IndexResource resource : resources) {
//
//					if (filterResource(resource, clusterID, indexID, scope, collectionID)) {
//
//						if (!serviceResources.containsKey(endpoint))
//							serviceResources.put(endpoint, new HashSet<String>());
//	
//						serviceResources.get(endpoint)
//								.add(resource.getResourceID());
//						
//					}
//				}
//			}
//
//			return serviceResources;
//
//		} catch (DiscovererException e) {
//			throw new IndexDiscoverException(
//					"error while discovering fulltextnode resources", e);
//		}
//	}
//
//	@Override
//	public Set<IndexResource> discoverFulltextIndexNodeResources(
//			String clusterID, String indexID, String collectionID, String scope)
//			throws IndexDiscoverException {
//
//		logger.info("calling discoverFulltextIndexNodeResources with parameters. clusterID : " + clusterID + ", indexID : " + indexID + ", scope : " + scope);
//		
//		Set<String> endpoints = this
//				.discoverFullTextNodeRunningInstances(scope);
//		try {
//			Map<String, Set<IndexResource>> foundResources = this
//					.discoverResources(endpoints, IndexResource.class, scope);
//
//			if (foundResources == null || foundResources.size() == 0) {
//				logger.warn("No resources found by the default discoverer");
//				return null;
//			}
//
//			Set<IndexResource> serviceResources = new HashSet<IndexResource>();
//
//			for (Entry<String, Set<IndexResource>> entry : foundResources
//					.entrySet()) {
//
//				Set<IndexResource> resources = entry.getValue();
//
//				for (IndexResource resource : resources) {
//
//					if (filterResource(resource, clusterID, indexID, scope, collectionID)){
//						serviceResources.add(resource);
//					}
//				}
//			}
//
//			return serviceResources;
//
//		} catch (DiscovererException e) {
//			throw new IndexDiscoverException(
//					"error while discovering fulltextnode resources", e);
//		}
//
//	}
//	
//	private static Boolean filterResource(IndexResource resource, String clusterID, String indexID, String scope, String collectionID){
//		
//		logger.info("calling filterResource with parameters. clusterID : " + clusterID + ", indexID : " + indexID + ", scope : " + scope + ", collectionID : " + collectionID);
//		logger.info("                      resource params : clusterID : " + resource.getClusterID() + ", indexID : " + resource.getIndexID() + ", scope : " + resource.getScope() + ", collectionIDs : " + resource.getCollections());
//		
//		if (clusterID != null){
//			if (resource.getClusterID() == null || !resource.getClusterID().equalsIgnoreCase(clusterID)){
//				logger.info("resource clusterID different than : "
//						+ clusterID + ". resource has : " + resource.getClusterID());
//				return false;
//			}
//		}
//		
//		if (indexID != null){
//			if (resource.getIndexID() == null || !resource.getIndexID().equalsIgnoreCase(indexID)){
//				logger.info("resource indexID different than : "
//						+ indexID + ". resource has : " + resource.getIndexID());
//				return false;
//			}
//		}
//		
//		if (scope != null){
//			if (resource.getScope() == null || !resource.getScope().equalsIgnoreCase(scope)){
//				logger.info("resource scope different than : "
//						+ scope + " .resource has : " + resource.getScope());
//				return false;
//			}
//		}
//		
//		if (collectionID != null){
//			if (resource.getCollections() == null || !resource.getCollections().contains(collectionID)){
//				logger.info("resource collections different than : "
//						+ collectionID + " .resource has : " + resource.getCollections());
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//	@Override
//	public Set<String> discoverFullTextNodeRunningInstances(String scope) {
//		return this.discoverRunningInstances(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, Constants.ENDPOINT_KEY, scope);
//	}
}
