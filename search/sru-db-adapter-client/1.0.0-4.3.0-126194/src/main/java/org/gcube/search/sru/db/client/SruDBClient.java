package org.gcube.search.sru.db.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.helpers.ResponseUtils;
import org.gcube.search.sru.db.client.exception.SruDBClientException;
import org.gcube.search.sru.db.client.inject.SruDBClientModule;
import org.gcube.search.sru.db.common.apis.SruDBServiceAPI;
import org.gcube.search.sru.db.common.discoverer.SruDBDiscovererAPI;
import org.gcube.search.sru.db.common.resources.SruDBResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class SruDBClient {

	private String endpoint;
	private String resourceID;
	private final String scope;
	
	private static final Logger logger = LoggerFactory
			.getLogger(SruDBClient.class);
	
	public static class Builder {
		private String endpoint;
		private String resourceID;
		private String scope;
		private boolean skipInitialize = false;
		
		private final SruDBDiscovererAPI<SruDBResource> discoverer;
		
		public Builder(){
			this.discoverer = null;
		}
		
		@Inject
		public Builder(SruDBDiscovererAPI<SruDBResource> discoverer){
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
		
		public Builder skipInitialize(boolean skipInitialize){
			this.skipInitialize  = skipInitialize;
			return this;
		}
		
		public SruDBClient build() throws SruDBClientException {
			return new SruDBClient(this);
		}
		
		
	}
	
	
	SruDBClient(Builder builder) throws SruDBClientException {
		this.endpoint = builder.endpoint;
		this.scope = builder.scope;
		this.resourceID = builder.resourceID;
		
		SruDBDiscovererAPI<SruDBResource> discoverer = builder.discoverer;
		
		if (discoverer == null) {
			Injector injector = Guice.createInjector(new SruDBClientModule());
			discoverer = injector.getInstance(Key.get(new TypeLiteral<SruDBDiscovererAPI<SruDBResource>>(){}));
		}
		
		if (builder.skipInitialize == true && this.scope != null && this.endpoint != null && this.resourceID != null){
			logger.info("requested to skip the initialize part");
		} else {
			this.initialize(discoverer);
		}
	}


	private void initialize(final SruDBDiscovererAPI<SruDBResource> discoverer) throws SruDBClientException {
		boolean found = false;
		
		try {
			
			Map<String, Set<String>> srudbNodes = discoverer
					.discoverSruDBNodes(this.scope, null);
			
			
			logger.info("running instances for index service : " + srudbNodes);
			
			List<String> endpoints = Lists.newArrayList(srudbNodes.keySet());
			
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					endpoints = Lists.newArrayList(this.endpoint);
				} else {
					throw new SruDBClientException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
				}
			} else {
				Collections.shuffle(endpoints);
			}
			
			for (String endpoint : endpoints) {
				
				if (srudbNodes.get(endpoint) != null &&
						srudbNodes.get(endpoint).size() > 0) {
					List<String> resources = Lists.newArrayList(srudbNodes.get(endpoint));
					
					if (this.resourceID != null) {
						if (resources.contains(this.resourceID)){
							resources = Lists.newArrayList(this.resourceID);
						} else {
							throw new SruDBClientException("could not initialize random client. given resourceID : " + this.resourceID + " found resourceIDs : " + resources);
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
			throw new SruDBClientException("could not initialize random client", e);
		}
		
		if (!found)
			throw new SruDBClientException("could not initialize random client");
	}
	
	
	
	private static SruDBServiceAPI getSruDBServiceProxy(String endpoint) throws SruDBClientException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		SruDBServiceAPI simple = null;
		
		logger.info("getting proxy from index factory service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			simple = target.proxy(SruDBServiceAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint, e);
			throw new SruDBClientException("Client could not connect to endpoint : " + endpoint, e);
		}
		
		logger.info("getting proxy from sru factory service...OK");
		
		return simple;
	}
	
	
	
	public String call(String operation, Float version, String recordPacking, String query, Integer maximumRecords, String recordSchema) throws SruDBClientException {
		
		logger.info("calling call with parameters. operation : " + operation + ", version : " + version + ", recordPacking : " + recordPacking + ", query : " + query + ", maximumRecords : " + maximumRecords + ", " + recordSchema);
		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
		
		Response response = getSruDBServiceProxy(this.endpoint)
				.get(this.scope, this.resourceID, operation, version, recordPacking, query, maximumRecords, recordSchema);
		

		logger.info("get returned");

		try {
			ResponseUtils.checkResponse(response, "get");
		} catch (Exception e) {
			throw new SruDBClientException(e);
		}

		String xml = response.readEntity(String.class);
		response.close();

		return xml;
	}
	
	
	public String explain() throws SruDBClientException {
		return this.call("explain", null, null, null, null, null);
	}
	
	public String searchRetrieve(Float version, String recordPacking, String query, Integer maximumRecords, String recordSchema) throws SruDBClientException {
		return this.call("searchRetrieve", version, recordPacking, query, maximumRecords, recordSchema);
	}
}
