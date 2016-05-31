package org.gcube.rest.index.service.resources;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.gcube.elasticsearch.FullTextNode;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.resources.IndexResource;
import org.gcube.rest.index.service.IndexClientWrapper;
import org.gcube.rest.index.service.IndexService;
import org.gcube.rest.resourceawareservice.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class IndexResourceFactory extends ResourceFactory<IndexResource> {

	private static final Logger logger = LoggerFactory
			.getLogger(IndexResourceFactory.class);

	private static final String DEFAULT_SAME_CLUSTER_PROP = "defaultSameCluster";
	private static final String SCOPE_PROP = "scope";
	
	private Map<String, IndexClientWrapper> indexClientWrappers = new ConcurrentHashMap<String, IndexClientWrapper>();

	private Provider<IndexClientWrapper> ftnClientProvider;
	private final IndexDiscovererAPI<IndexResource> indexDiscoverer;
	private final Provider<IndexClient.Builder> clientProvider;
	
	
	@Inject
	public IndexResourceFactory(Provider<IndexClientWrapper> ftnClientProvider,
			IndexDiscovererAPI<IndexResource> indexDiscoverer,
			Provider<IndexClient.Builder> clientProvider) {
		this.ftnClientProvider = ftnClientProvider;
		this.indexDiscoverer = indexDiscoverer;
		this.clientProvider = clientProvider;
	}

	public FullTextNode getIndexNode(IndexResource resource)
			throws ResourceNotFoundException {
		if (!this.indexClientWrappers.containsKey(resource.getResourceID()))
			throw new ResourceNotFoundException("resource with id : "
					+ resource.getResourceID() + " not in map factory : "
					+ this.indexClientWrappers.keySet());
		return this.indexClientWrappers.get(resource.getResourceID())
				.getFullTextNode();
	}

	@Override
	public IndexResource createResource(String resourceID, String params)
			throws StatefulResourceException {
		logger.info("IndexResource createResource");

		IndexResource resource = new Gson().fromJson(params,
				IndexResource.class);

		if (resource.getScope() != null
				&& resource.getScope().equalsIgnoreCase(this.getScope()) == false) {
			logger.error("scope set to : " + resource.getScope()
					+ " but different to : " + this.getScope());
			throw new StatefulResourceException("scope set to : "
					+ resource.getScope() + " but different to : "
					+ this.getScope());
		}

		resource.setResourceID(resourceID);

		logger.info("IndexClientWrapper initializing");

		IndexClientWrapper clientWrapper = this.ftnClientProvider.get();
		initICWToResource(clientWrapper, resource, this.getScope(), this.indexDiscoverer, this.clientProvider);
		this.indexClientWrappers.put(resourceID, clientWrapper);

		resource.setHostname(clientWrapper.getFullTextNode().getHostname());
		logger.info("hostname set to resource : " + resource.getHostname());

		return resource;
	}

	@Override
	public void loadResource(IndexResource resource)
			throws StatefulResourceException {
		logger.info("IndexResource loadResource");

		if (resource.getScope() != null
				&& resource.getScope().equalsIgnoreCase(this.getScope()) == false) {
			logger.error("scope set to : " + resource.getScope()
					+ " but different to : " + this.getScope());
			throw new StatefulResourceException("scope set to : "
					+ resource.getScope() + " but different to : "
					+ this.getScope());
		}

		logger.info("IndexClientWrapper loading");
		IndexClientWrapper clientWrapper = this.ftnClientProvider.get();
		loadICWToResource(clientWrapper, resource, this.getScope(), this.indexDiscoverer, this.clientProvider);

		resource.setHostname(clientWrapper.getFullTextNode().getHostname());
		logger.info("hostname set to resource : " + resource.getHostname());

		resource.onLoad();
		this.indexClientWrappers.put(resource.getResourceID(), clientWrapper);
	}

	@Override
	public void closeResource(IndexResource resource)
			throws StatefulResourceException {
		IndexClientWrapper icw = this.indexClientWrappers.get(resource
				.getResourceID());
		logger.info("Closing index...");
		icw.getFullTextNode().close();
		logger.info("Closing index...OK");
		this.indexClientWrappers.remove(resource.getResourceID());
	}

	@Override
	public void destroyResource(IndexResource resource)
			throws StatefulResourceException {

		this.closeResource(resource);
		//TODO: should we delete the index when a resource is destroyed?
		/*IndexClientWrapper icw = this.indexClientWrappers.get(resource
				.getResourceID());

		 try {
			logger.info("Deleting index");
			icw.getFullTextNode().deleteIndex();
		} catch (Exception e) {
			logger.error(
					"Error while deleting the index. Maybe it does not exist",
					e);
			throw new StatefulResourceException(
					"Error while deleting the index. Maybe it does not exist",
					e);
		} finally {
			this.closeResource(resource);
		} */
	}

	private static void initICWToResource(
			IndexClientWrapper indexClientWrapper, IndexResource resource, String scope,
			IndexDiscovererAPI<IndexResource> indexDiscoverer,
			Provider<IndexClient.Builder> clientProvider)
			throws StatefulResourceException {
		if (resource.getIndexID() == null
				|| resource.getIndexID().trim().length() == 0) {
			logger.info("No indexID given, assigning a new one: "
					+ resource.getResourceID());
			resource.setIndexID(resource.getResourceID());
		}

		String transformedClusterName = transformClusterName(
				resource.getClusterID(), resource.getResourceID(),
				resource.getScope());
		
		resource.setClusterID(changeClusterID(resource.getClusterID(), resource.getResourceID(),resource.getScope()));

		try {
			logger.info("initializing FullTextNodeClient : ");
			indexClientWrapper.initialize(transformedClusterName, resource.getClusterID(), scope);
		} catch (Exception e) {
			throw new StatefulResourceException(
					"error while initializing the fulltext index client", e);
		}

		String transportAddress = indexClientWrapper.getFullTextNode()
				.getESTransportAddress();
		resource.setEsTransportAddress(transportAddress);

		//not needed in create since the meta index will be likely consistent
//		logger.info("recreating meta index from data to resolve inconsistencies");
//		indexClientWrapper.getFullTextNode().recreateMetaIndex();
		
		List<String> collectionsOfMetaIndex = null;
		List<String> fieldsOfMetaIndex = null;
		try {
			collectionsOfMetaIndex = indexClientWrapper
					.getFullTextNode().getCollectionsFromMeta();
			fieldsOfMetaIndex = indexClientWrapper.getFullTextNode()
					.getFieldsFromMeta();
			
		} catch (Exception e) {
			logger.warn("query to the meta index failed. Collection and fields will be considered null and will be updated be the next update", e);
		}
		if (collectionsOfMetaIndex != null && fieldsOfMetaIndex != null) {
			resource.setCollections(collectionsOfMetaIndex);
			resource.setFields(fieldsOfMetaIndex);
//			try {
//				IndexService.updateAllResourcesCollectionsAndFields(indexClientWrapper.getFullTextNode(), clientProvider.get(), resource.getClusterID(), indexDiscoverer, scope);
//			} catch (Exception e) {
//				logger.warn("problem while updating all the available resources : ", e);
//			}
		} else {
			resource.setCollections(Collections.<String> emptyList());
			resource.setFields(Collections.<String> emptyList());
		}

		resource.setSupportedRelations(IndexResource.getSupportedRelationsSet());

	}

	private static void loadICWToResource(
			IndexClientWrapper indexClientWrapper, IndexResource resource, String scope,
			IndexDiscovererAPI<IndexResource> indexDiscoverer,
			Provider<IndexClient.Builder> clientProvider)
			throws StatefulResourceException {

		String transformedClusterName = transformClusterName(
				resource.getClusterID(), resource.getResourceID(),
				resource.getScope());
		
		resource.setClusterID(changeClusterID(resource.getClusterID(), resource.getResourceID(),resource.getScope()));
		
		try {
			logger.info("initializing FullTextNodeClient : ");
			indexClientWrapper.initialize(transformedClusterName, resource.getClusterID(), scope);
		} catch (Exception e) {
			throw new StatefulResourceException(
					"error while initializing the fulltext index client", e);
		}

		String transportAddress = indexClientWrapper.getFullTextNode()
				.getESTransportAddress();
		resource.setEsTransportAddress(transportAddress);

//		logger.info("recreating meta index from data to resolve inconsistencies");
//		indexClientWrapper.getFullTextNode().recreateMetaIndex();
		
		logger.info("getting meta index values for existing index");
		List<String> collectionsOfMetaIndex = null;
		List<String> fieldsOfMetaIndex = null;
		try {
			collectionsOfMetaIndex = indexClientWrapper
					.getFullTextNode().getCollectionsFromMeta();
			fieldsOfMetaIndex = indexClientWrapper.getFullTextNode()
					.getFieldsFromMeta();
			
		} catch (Exception e) {
			logger.warn("query to the meta index failed. Collection and fields will be considered null and will be updated be the next update", e);
		}
		if (collectionsOfMetaIndex != null && fieldsOfMetaIndex != null) {
			resource.setCollections(collectionsOfMetaIndex);
			resource.setFields(fieldsOfMetaIndex);
			try {
				IndexService.updateAllResourcesCollectionsAndFields(indexClientWrapper.getFullTextNode(), clientProvider.get(), resource.getClusterID(), indexDiscoverer, scope);
			} catch (Exception e) {
				logger.warn("problem while updating all the available resources : ", e);
			}
		} else {
			resource.setCollections(Collections.<String> emptyList());
			resource.setFields(Collections.<String> emptyList());
		}

		resource.setSupportedRelations(IndexResource.getSupportedRelationsSet());
	}

	private static String transformClusterName(String initClusterID,
			String resourceID, String scope) {

		logger.info("calling transformedClusterName for parameters. initClusterID : "
				+ initClusterID
				+ ", resourceID : "
				+ resourceID
				+ ", scope : "
				+ scope);

		String clusterName = changeClusterID(initClusterID, resourceID, scope);

		String ret = "es-cluster-" + scope + "-" + clusterName;
		
		ret = ret.replace("/", "-");
		logger.info("transformedClusterName : " + ret);

		return ret;

	}
	
	static String changeClusterID(String initClusterID,
			String resourceID, String scope){
		
		final Properties properties = new Properties();
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE)
				.openStream()) {
			properties.load(is);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"could not load property file  : "
							+ Constants.PROPERTIES_FILE);
		}

		Boolean defaultSameCluster = Boolean.valueOf(properties
				.getProperty(DEFAULT_SAME_CLUSTER_PROP));

		String clusterName = null;

		if (initClusterID != null) {
			clusterName = initClusterID;
		} else {
			if (defaultSameCluster == true) {
				clusterName = "default-cluster-name";
			} else {
				clusterName = resourceID;
			}
		}
		
		return clusterName;
	}

	@Override
	public String getScope() {
		final Properties properties = new Properties();
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE)
				.openStream()) {
			properties.load(is);
			
			return properties.getProperty(SCOPE_PROP);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"could not load property file  : "
							+ Constants.PROPERTIES_FILE);
		}
	}

}
