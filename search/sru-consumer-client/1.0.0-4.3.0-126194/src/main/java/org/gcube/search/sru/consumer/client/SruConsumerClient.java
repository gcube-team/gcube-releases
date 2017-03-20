package org.gcube.search.sru.consumer.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.helpers.ResponseUtils;
import org.gcube.rest.commons.helpers.ResultReader;
import org.gcube.search.sru.consumer.client.exception.SruConsumerClientException;
import org.gcube.search.sru.consumer.client.inject.SruConsumerClientModule;
import org.gcube.search.sru.consumer.common.apis.SruConsumerServiceAPI;
import org.gcube.search.sru.consumer.common.discoverer.SruConsumerDiscovererAPI;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;
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

public class SruConsumerClient {
	private static Gson gson = new Gson();

	private String endpoint;
	private String resourceID;
	private String collectionID;
	private final String scope;
	
	
	
	public String getEndpoint() {
		return endpoint;
	}


	public String getResourceID() {
		return resourceID;
	}


	public String getCollectionID() {
		return collectionID;
	}


	public String getScope() {
		return scope;
	}

	private static final Logger logger = LoggerFactory
			.getLogger(SruConsumerClient.class);
	
	public static class Builder {
		private String endpoint;
		private String resourceID;
		private String collectionID;
		private String scope;
		private boolean skipInitialize = false;
		
		private final SruConsumerDiscovererAPI<SruConsumerResource> discoverer;
		
		public Builder(){
			this.discoverer = null;
		}
		
		@Inject
		public Builder(SruConsumerDiscovererAPI<SruConsumerResource> discoverer){
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
		
		public Builder collectionID(String collectionID){
			this.collectionID = collectionID;
			return this;
		}
		
		public Builder scope(String scope){
			this.scope = scope;
			return this;
		}
		
		public Builder skipInitialize(boolean skipInitialize){
			this.skipInitialize  = skipInitialize;
			return this;
		}
		
		public SruConsumerClient build() throws SruConsumerClientException {
			return new SruConsumerClient(this);
		}
		
		
	}
	
	
	SruConsumerClient(Builder builder) throws SruConsumerClientException {
		this.endpoint = builder.endpoint;
		this.scope = builder.scope;
		this.resourceID = builder.resourceID;
		this.collectionID = builder.collectionID;
		
		SruConsumerDiscovererAPI<SruConsumerResource> discoverer = builder.discoverer;
		
		if (discoverer == null) {
			Injector injector = Guice.createInjector(new SruConsumerClientModule());
			discoverer = injector.getInstance(Key.get(new TypeLiteral<SruConsumerDiscovererAPI<SruConsumerResource>>(){}));
		}
		if (builder.skipInitialize == true && this.scope != null && this.endpoint != null && this.resourceID != null){
			logger.info("requested to skip the initialize part");
		} else {
			this.initialize(discoverer);
		}
	}


	private void initialize(final SruConsumerDiscovererAPI<SruConsumerResource> discoverer) throws SruConsumerClientException {
		boolean found = false;
		
		try {
			
			Map<String, Set<String>> SruConsumerNodes = discoverer
					.discoverSruConsumerNodes(this.scope, this.collectionID);
			
			
			logger.info("running instances for sru consumer service : " + SruConsumerNodes);
			
			List<String> endpoints = Lists.newArrayList(SruConsumerNodes.keySet());
			
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					endpoints = Lists.newArrayList(this.endpoint);
				} else {
					throw new SruConsumerClientException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
				}
			} else {
				Collections.shuffle(endpoints);
			}
			
			for (String endpoint : endpoints) {
				
				if (SruConsumerNodes.get(endpoint) != null &&
						SruConsumerNodes.get(endpoint).size() > 0) {
					List<String> resources = Lists.newArrayList(SruConsumerNodes.get(endpoint));
					
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
			throw new SruConsumerClientException("could not initialize random client", e);
		}
		
		if (!found) {
			if (this.resourceID != null)
				throw new SruConsumerClientException("could not initialize random client. given resourceID : " + this.resourceID);
			else
				throw new SruConsumerClientException("could not initialize random client");
		}
	}
	
	
	
	private static SruConsumerServiceAPI getSruConsumerServiceProxy(String endpoint) throws SruConsumerClientException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		SruConsumerServiceAPI simple = null;
		
		logger.info("getting proxy from index factory service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			simple = target.proxy(SruConsumerServiceAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint, e);
			throw new SruConsumerClientException("Client could not connect to endpoint : " + endpoint, e);
		}
		
		logger.info("getting proxy from sru factory service...OK");
		
		return simple;
	}
	
	public String query(String query, Long maxRecords) throws SruConsumerClientException {
		return query(query, maxRecords, true);
	}
	
	public String query(String query, Long maxRecords, Boolean useRR) throws SruConsumerClientException {
		
		logger.info("calling query with parameters. query : " + query + " , maxRecords : " + maxRecords + ", useRR : " + useRR);
		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
		
		
		
		Response response = getSruConsumerServiceProxy(this.endpoint)
				.query(this.scope, this.resourceID, query, maxRecords, false, useRR);
		
		logger.info("queryAndRead returned");
		
		try {
			ResponseUtils.checkResponse(response, "query");
		} catch (Exception e) {
			throw new SruConsumerClientException(e);
		}

		String json = response.readEntity(String.class);
		response.close();
		
		Map<String, String> resp = gson.fromJson(json,
				new TypeToken<Map<String, String>>() {
				}.getType());

		return resp.get("grslocator");
	}
	
	
	public String query(String query) throws SruConsumerClientException{
		return this.query(query, null);
	}
	
//	public String query(String operation, Float version, String recordPacking, String query, Long maxRecords) throws SruConsumerClientException {
//		
//		logger.info("calling query with parameters. query : " + query);
//		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
//		
//		Response response = getSruConsumerServiceProxy(this.endpoint)
//				.query(this.scope, this.resourceID, query, maxRecords, false);
//		
//
//		logger.info("query returned");
//
//		try {
//			ResponseUtils.checkResponse(response, "query");
//		} catch (Exception e) {
//			throw new SruConsumerClientException(e);
//		}
//
//		String xml = response.readEntity(String.class);
//		response.close();
//
//		return xml;
//	}
	
	public List<Map<String, String>> queryAndRead(String query) throws SruConsumerClientException{
		return this.queryAndRead(query, null, true);
	}
	
	public List<Map<String, String>> queryAndRead(String query, Boolean useRR) throws SruConsumerClientException{
		return this.queryAndRead(query, null, useRR);
	}
	
	public List<Map<String, String>> queryAndRead(String query, Long maxRecords, Boolean useRR) throws SruConsumerClientException {
		
		logger.info("calling queryAndRead with parameters. query : " + query + " , maxRecords : " + maxRecords + " , useRR : " + useRR);
		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
		
		Response response = getSruConsumerServiceProxy(this.endpoint)
				.query(this.scope, this.resourceID, query, maxRecords, true, useRR);
		
		logger.info("queryAndRead returned");
		
		try {
			ResponseUtils.checkResponse(response, "queryAndRead");
		} catch (Exception e) {
			throw new SruConsumerClientException(e);
		}

		String json = response.readEntity(String.class);
		response.close();
		
		try {
			List<Map<String, String>> resp = gson
					.fromJson(json, new TypeToken<List<Map<String, String>>>() {}
					.getType());
			return resp;
		} catch (Exception e) {
			logger.error("Unable to convert response to list of maps of strings : " + json);
			throw new SruConsumerClientException("Unable to convert response to list of maps of strings " + json, e);
		}
	}
	
	public List<Map<String, String>> queryAndReadClientSide(String query) throws SruConsumerClientException {
		return this.queryAndReadClientSide(query, null, true);
	}
	
	public List<Map<String, String>> queryAndReadClientSide(String query, Long maxResults, Boolean useRR) throws SruConsumerClientException {
		logger.info("calling queryAndReadClientSide with parameters. query : " + query + ", maxResults : " + maxResults + " , useRR : " + useRR);
		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
		
		String grsLocator = this.query(query, maxResults, useRR);
		
		logger.info("queryAndReadClientSide returned : " + grsLocator);
		
		try {
			List<Map<String, String>> results = ResultReader.resultSetToRecords(grsLocator);
			
			return results;
		} catch (Exception e) {
			logger.error("could not read results from the grs2 locator : " + grsLocator, e);
			throw new SruConsumerClientException("could not read results from the grs2 locator : " + grsLocator, e);
		}
	}
	
//	public String query(String query, Long maxRecords) throws SruConsumerClientException {
//		
//		logger.info("calling query with parameters. query : " + query + " , maxRecords : " + maxRecords + ", result : " + result);
//		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
//		
//		Response response = getSruConsumerServiceProxy(this.endpoint)
//				.query(this.scope, this.resourceID, query, maxRecords, false);
//		
//
//		logger.info("query returned");
//
//		try {
//			ResponseUtils.checkResponse(response, "query");
//		} catch (Exception e) {
//			throw new SruConsumerClientException(e);
//		}
//
//		String xml = response.readEntity(String.class);
//		response.close();
//
//		return xml;
//	}
	
	
	public String explain() throws SruConsumerClientException {
		logger.info("calling explain with parameters. none");
		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
		
		Response response = getSruConsumerServiceProxy(this.endpoint)
				.explain(this.scope, this.resourceID);
		

		logger.info("explain returned");

		try {
			ResponseUtils.checkResponse(response, "explain");
		} catch (Exception e) {
			throw new SruConsumerClientException(e);
		}

		String xml = response.readEntity(String.class);
		response.close();

		return xml;
	}
}
