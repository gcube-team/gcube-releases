package org.gcube.rest.opensearch.client;

import static org.gcube.rest.opensearch.client.helpers.ResponseUtils.checkResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.gcube.rest.opensearch.client.exception.OpenSearchClientException;
import org.gcube.rest.opensearch.client.inject.OpenSearchClientModule;
import org.gcube.rest.opensearch.common.apis.OpenSearchServiceAPI;
import org.gcube.rest.opensearch.common.discover.OpenSearchDiscovererAPI;
import org.gcube.rest.opensearch.common.helpers.ResultReader;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;


public class OpenSearchClient {

	private static Gson gson = new Gson();

	private String endpoint;
	private String resourceID;
	private final String scope;
	
	private final String collectionID;
	
	private static final Logger logger = LoggerFactory
			.getLogger(OpenSearchClient.class);
	
	
	public static class Builder {
		private String endpoint;
		private String resourceID;
		private String scope;
		private String collectionID;
		
		private final OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer;
		
		public Builder(){
			this.discoverer = null;
		}
		
		@Inject
		public Builder(OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer){
			this.discoverer = discoverer;
		}
		
		public Builder endpoint(String endpoint){
			if (endpoint.endsWith("/"))
				endpoint = endpoint.substring(0, endpoint.length()-1);
			
			this.endpoint = endpoint;
			return this;
		}
		
		public Builder resourceID(String resourceID){
			this.resourceID = resourceID;
			return this;
		}
		
		public Builder scope(String scope){
			this.scope = scope;
			return this;
		}
		
		public Builder collectionID(String collectionID){
			this.collectionID = collectionID;
			return this;
		}
		
		public OpenSearchClient build() throws OpenSearchClientException {
			return new OpenSearchClient(this);
		}
	}
	
	
	public OpenSearchClient(Builder builder) throws OpenSearchClientException {
		this.endpoint = builder.endpoint;
		this.scope = builder.scope;
		this.resourceID = builder.resourceID;
		this.collectionID = builder.collectionID;
		
		OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer = builder.discoverer;
		
		if (discoverer == null) {
			Injector injector = Guice.createInjector(new OpenSearchClientModule());
			discoverer = injector.getInstance(Key.get(new TypeLiteral<OpenSearchDiscovererAPI<OpenSearchDataSourceResource>>(){}));
		}
		
		this.initialize(discoverer);
	}
	
	private final void initialize(OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer) throws OpenSearchClientException {
		boolean found = false;
		
		try {
			Map<String, Set<String>> opensearchNodes = discoverer
					.discoverOpenSearchNodes(this.collectionID, this.scope);
			
			logger.info("running instances for opensearch service : " + opensearchNodes);
	
			List<String> endpoints = Lists.newArrayList(opensearchNodes.keySet());
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					endpoints = Lists.newArrayList(this.endpoint);
				} else {
					throw new OpenSearchClientException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
				}
			} else {
				Collections.shuffle(endpoints);
			}
			
			
			for (String endpoint : endpoints) {
			
				if (opensearchNodes.get(endpoint) != null &&
						opensearchNodes.get(endpoint).size() > 0) {
					List<String> resources = Lists.newArrayList(opensearchNodes.get(endpoint));
					
					if (this.resourceID != null) {
						if (resources.contains(this.resourceID)){
							resources = Lists.newArrayList(this.resourceID);
						} else {
							logger.info("resource : " + this.resourceID + " not in : "  + resources + " of " + endpoint);
							continue;
						}
					} else {
						Collections.shuffle(resources);
					}
					
					this.resourceID = resources.get(0);
					this.endpoint = endpoint;
					found = true;
					
					break;
				}
			}
			
			logger.info("Initialized at : " + this.endpoint + " , "
					+ this.resourceID);
			
		} catch (Exception e) {
			logger.error("could not initialize random client", e);
			throw new OpenSearchClientException("could not initialize random client", e);
		}
		
		if (!found){
			if (this.resourceID != null)
				throw new OpenSearchClientException("could not initialize random client. given resourceID : " + this.resourceID);
			else
				throw new OpenSearchClientException("could not initialize random client");
		}
	} 
	
	public String getEndpoint(){
		return this.endpoint;
	}
	
	public String getResourceID(){
		return this.resourceID;
	}
	
	public String getScope() {
		return this.scope;
	}
	
	public String getCollectionID() {
		return this.collectionID;
	}
	
	
	private static OpenSearchServiceAPI getOpenSearchServiceProxy(
			String endpoint) throws OpenSearchClientException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		OpenSearchServiceAPI simple = null;

		logger.info("getting proxy from opensearch service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			simple = target.proxy(OpenSearchServiceAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint,
					e);
			throw new OpenSearchClientException("Client could not connect to endpoint : "
					+ endpoint, e);
		}

		logger.info("getting proxy from opensearch service...OK");
		
		return simple;
	}
	
	public String query(String queryString) throws OpenSearchClientException{
		return query(queryString, true);
	}
	
	public String query(String queryString, Boolean useRR) throws OpenSearchClientException {
		
		logger.info("calling query with parameters. queryString : " + queryString + ", useRR : " + useRR);
		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
		
		Response response = getOpenSearchServiceProxy(this.endpoint)
				.query(this.scope, this.resourceID, queryString, useRR, false, false, false);
		
		logger.info("query returned");

		checkResponse(response, "query");

		String json = response.readEntity(String.class);
		response.close();

		Map<String, String> resp = gson.fromJson(json,
				new TypeToken<Map<String, String>>() {
				}.getType());

		return resp.get("grslocator");
	}
	
	public List<Map<String, String>> queryAndReadClientSide(String queryString) throws OpenSearchClientException{
		return queryAndReadClientSide(queryString, true);
	}
	
	public List<Map<String, String>> queryAndReadClientSide(String queryString, Boolean useRR) throws OpenSearchClientException {
		logger.info("calling queryAndReadClientSide with parameters. queryString : " + queryString + ", useRR : " + useRR);
		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
		
		String grsLocator = this.query(queryString, useRR);
		
		logger.info("queryAndReadClientSide returned : " + grsLocator);
		
		try {
			List<Map<String, String>> results = ResultReader.resultSetToRecords(grsLocator);
			
			return results;
		} catch (Exception e) {
			logger.error("could not read results from the grs2 locator : " + grsLocator, e);
			throw new OpenSearchClientException("could not read results from the grs2 locator : " + grsLocator, e);
		}
	}
	
	public List<Map<String, String>> queryAndRead(String queryString) throws OpenSearchClientException{
		return queryAndRead(queryString, true);
	}
	
	public List<Map<String, String>> queryAndRead(String queryString, Boolean useRR) throws OpenSearchClientException {
		logger.info("calling query with parameters. queryString : " + queryString + ", useRR : " + useRR);
		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
		
		Response response = getOpenSearchServiceProxy(this.endpoint)
				.query(this.scope, this.resourceID, queryString, useRR, true, false, false);
		
		logger.info("query returned");

		checkResponse(response, "query");

		String json = response.readEntity(String.class);
		response.close();

		try {
			List<Map<String, String>> resp = gson.fromJson(json,
					new TypeToken<List<Map<String, String>>>() {
					}.getType());
			return resp;
		} catch (Exception e) {
			logger.error("Unable to convert response to list of maps of strings : " + json);
			throw new OpenSearchClientException("Unable to convert response to list of maps of strings " + json, e);
		}
	}

}
