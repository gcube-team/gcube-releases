package org.gcube.rest.index.client.factory;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.gcube.rest.index.client.exceptions.IndexException;
import org.gcube.rest.index.client.inject.IndexClientModule;
import org.gcube.rest.index.common.apis.IndexFactoryAPI;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.resources.IndexResource;
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

public class IndexFactoryClient {
	private static Gson gson = new Gson();

	private String endpoint;
	
	private static final Logger logger = LoggerFactory.getLogger(IndexFactoryClient.class);
	
	public static class Builder {
		private String endpoint;
		private String scope;
		
		private boolean skipInitialization = false;
		
		private final IndexDiscovererAPI<IndexResource> indexDiscoverer;
		
		@Inject
		public Builder(IndexDiscovererAPI<IndexResource> indexDiscoverer){
			this.indexDiscoverer = indexDiscoverer;
		}
		
		public Builder(){
			this.indexDiscoverer = null;
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
		
		public Builder skipInitialization(boolean skipInitialization){
			this.skipInitialization = skipInitialization;
			return this;
		}
		
		public IndexFactoryClient build() throws IndexException{
			return new IndexFactoryClient(this);
		}
	}
	
	public IndexFactoryClient(Builder builder) throws IndexException{
		this.endpoint = builder.endpoint;
		
		String scope = builder.scope;
		
		IndexDiscovererAPI<IndexResource> indexDiscoverer = builder.indexDiscoverer;
		if (indexDiscoverer == null) {
			Injector injector = Guice.createInjector(new IndexClientModule());
			indexDiscoverer = injector.getInstance(Key.get(new TypeLiteral<IndexDiscovererAPI<IndexResource>>(){}));
		}
		
		if (!builder.skipInitialization)
			this.intialize(indexDiscoverer, scope);
	}
	
	
	private final void intialize(IndexDiscovererAPI<IndexResource> indexDiscoverer, String scope) throws IndexException {
		try {
			Set<String> fulltextIndexNodes = indexDiscoverer.discoverFullTextNodeRunningInstances(scope);
	
			List<String> endpoints = Lists.newArrayList(fulltextIndexNodes);
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					endpoints = Lists.newArrayList(this.endpoint);
				} else {
					throw new IndexException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
				}
			} else {
				Collections.shuffle(endpoints);
			}
			
			this.endpoint = endpoints.get(0);
			
			logger.info("Initialized at : " + this.endpoint);
		} catch (Exception e) {
			logger.error("could not initialize random client", e);
			throw new IndexException("could not initialize random client", e);
		}
		
	}

	public String createResource(String clusterID) throws IndexException {
		logger.warn("CALLING createResource without scope! this will create an error later because it will not be discovered by the IS");
		return this.createResource(clusterID, null);
	}
	
	public String createResource(String clusterID, String scope) throws IndexException {
		try {
			logger.info("calling createResourcee with parameters. clusterID : " + clusterID + ", scope : " + scope);
			
			IndexResource resource = new IndexResource();
			resource.setClusterID(clusterID);
			if (scope != null)
				resource.setScope(scope);
			
			String json = resource.toJSON();
			
			logger.info("calling create resource with json params : " + json);
			
			Response response = null;
			
			response = getFullTextIndexFactoryProxy(this.endpoint).createResourceREST(scope, json);
			
			logger.info("createResource returned");
			
			if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
				String error = response.readEntity(String.class);
				response.close();
				throw new IndexException("resource could not be created : " + error);
			}
			
			String responseJSON = response.readEntity(String.class);
			response.close();
			
			Map<String, String> resp = gson.fromJson(responseJSON,
					new TypeToken<Map<String, String>>() {
					}.getType());
			logger.info("Created resource with id : " + resp.get("resourceID"));
			
			String resourceID = resp.get("resourceID");
			
			return resourceID;
		} catch (Exception e) {
			throw new IndexException("error while creating resource", e);
		}
	}
	
	private static IndexFactoryAPI getFullTextIndexFactoryProxy(String endpoint) throws IndexException{
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		IndexFactoryAPI simple = null;
		
		logger.info("getting proxy from index factory service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			simple = target.proxy(IndexFactoryAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint, e);
			throw new IndexException("Client could not connect to endpoint : " + endpoint, e);
		}
		
		logger.info("getting proxy from index factory service...OK");
		
		return simple;
	}

	
	public String getEndpoint() {
		return this.endpoint;
	}

}
