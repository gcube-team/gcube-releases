package org.gcube.rest.opensearch.client.factory;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.opensearch.client.exception.OpenSearchClientException;
import org.gcube.rest.opensearch.client.inject.OpenSearchClientModule;
import org.gcube.rest.opensearch.common.apis.OpenSearchServiceFactoryAPI;
import org.gcube.rest.opensearch.common.discover.OpenSearchDiscovererAPI;
import org.gcube.rest.opensearch.common.entities.Provider;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource.FixedParam;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class OpenSearchFactoryClient {
	private static Gson gson = new Gson();
	private InformationCollector icollector;
	
	private String endpoint;
	
	private static final Logger logger = LoggerFactory.getLogger(OpenSearchFactoryClient.class);
	
	public static class Builder {
		private String endpoint;
		private String scope;
		
		private final OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer;
		private final InformationCollector icollector;
		
		public Builder(){
			this.discoverer = null;
			this.icollector = null;
		}
		
		@Inject
		public Builder(OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer, InformationCollector icollector){
			this.discoverer = discoverer;
			this.icollector = icollector;
		}
		
		public Builder endpoint(String endpoint){
			if (endpoint.endsWith("/"))
				endpoint = endpoint.substring(0, endpoint.length()-1);
			
			this.endpoint = endpoint;
			return this;
		}
		
		public Builder scope(String scope){
			this.scope = scope;
			return this;
		}
		
		public OpenSearchFactoryClient build() throws OpenSearchClientException {
			return new OpenSearchFactoryClient(this);
		}
		
	}
	
	public OpenSearchFactoryClient(Builder builder) throws OpenSearchClientException {
		this.endpoint = builder.endpoint;
		this.icollector = builder.icollector;
		OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer = builder.discoverer; 

		// check if injector needs to be created
		if (discoverer == null || this.icollector == null){
			Injector injector = Guice.createInjector(new OpenSearchClientModule());
			
			if (discoverer == null)
				discoverer = injector.getInstance(Key.get(new TypeLiteral<OpenSearchDiscovererAPI<OpenSearchDataSourceResource>>(){}));
			
			if (this.icollector == null)
				this.icollector = injector.getInstance(InformationCollector.class);
		}
		
		this.initialize(discoverer, builder.scope);
	}
	
	public String getEndpoint() {
		return this.endpoint;
	}
	
	private final void initialize(OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer, String scope) throws OpenSearchClientException {
		try {
			Set<String> opensearchNodes = discoverer.discoverOpenSearchInstances(scope);
	
			List<String> endpoints = Lists.newArrayList(opensearchNodes);
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					endpoints = Lists.newArrayList(this.endpoint);
				} else {
					throw new OpenSearchClientException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
				}
			} else {
				Collections.shuffle(endpoints);
			}
			
			this.endpoint = endpoints.get(0);
			
			logger.info("Initialized at : " + this.endpoint);
		} catch (Exception e) {
			logger.error("could not initialize random client", e);
			throw new OpenSearchClientException("could not initialize random client", e);
		}
		
	}
	
	
	
	public String createResource(final List<String> fields, final  List<Provider> providers, final  String scope) throws OpenSearchClientException {
		logger.info("calling createResourcee with parameters. scope : " + scope);
		
		OpenSearchDataSourceResource resource = new OpenSearchDataSourceResource();
		if (scope != null)
			resource.setScope(scope);
		
		////// OpenSearchDatasource#create same logic
		
		List<String> openSearchResourceIDs = Lists.newArrayList();
		List<String> collectionIDs = Lists.newArrayList();
		List<FixedParam> fixedParameters = Lists.newArrayList();
		
		for (Provider provider : providers){
			openSearchResourceIDs.add(provider.getOpenSearchResourceID());
			collectionIDs.add(provider.getCollectionID());
			
			FixedParam fp = new FixedParam();
			fp.setParams(Lists.newArrayList(provider.getFixedParameters()));
			
			fixedParameters.add(fp);
		}
		
		List<String> genericResources = getOpenSearchGenericResources(this.icollector, openSearchResourceIDs, scope);
		
		resource.setOpenSearchResourceXML(genericResources);
		
		resource.setOpenSearchResource(openSearchResourceIDs);
		
		resource.setCollectionID(collectionIDs);
		
		resource.setFixedParameters(fixedParameters);
		
		resource.setFields(fields);
		
		String json = resource.toJSON();
		
		logger.info("calling create resource with json params : " + json);
		
		Response response = null;
		
		response = getOpenSearchServiceFactoryProxy(this.endpoint).createResourceREST(scope, json);
		
		logger.info("createResource returned");
		
		if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
			String error = response.readEntity(String.class);
			response.close();
			throw new OpenSearchClientException("resource could not be created : " + error);
		}
		
		String responseJSON = response.readEntity(String.class);
		response.close();
		
		Map<String, String> resp = gson.fromJson(responseJSON,
				new TypeToken<Map<String, String>>() {
				}.getType());
		logger.info("Created resource with id : " + resp.get("resourceID"));
		
		String resourceID = resp.get("resourceID");
		
		return resourceID;
	}
	
	private static OpenSearchServiceFactoryAPI getOpenSearchServiceFactoryProxy(String endpoint) throws OpenSearchClientException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		OpenSearchServiceFactoryAPI simple = null;
		
		logger.info("getting proxy from opensearch factory service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			simple = target.proxy(OpenSearchServiceFactoryAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint, e);
			throw new OpenSearchClientException("Client could not connect to endpoint : " + endpoint, e);
		}
		
		logger.info("getting proxy from opensearch factory service...OK");
		
		return simple;
	}
	
	
	static public List<String> getOpenSearchGenericResources(InformationCollector icollector, List<String> resourceIDs, String scope){
		
		List<String> openSearchResourcesString = Lists.newArrayList();
		
		for (String resourceID : resourceIDs){
			
			List<Resource> resources = icollector.getGenericResourcesByID(resourceID, scope);
			
			if (resources != null && resources.size() > 0){
				
				try {
					String resourceBody = resources.get(0).getBodyAsString();
					
					if (resourceBody != null){
						openSearchResourcesString.add(resourceBody);
					}
				} catch (Exception e) {
					logger.warn("error reading the body for resource with id : " + resourceID, e);
				}
				
			}
		}
		
		return openSearchResourcesString;
	}
	
}
