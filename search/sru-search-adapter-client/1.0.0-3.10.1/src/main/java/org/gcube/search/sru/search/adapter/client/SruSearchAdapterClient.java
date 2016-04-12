package org.gcube.search.sru.search.adapter.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.helpers.ResponseUtils;
import org.gcube.search.sru.search.adapter.client.exception.SruSearchAdapterClientException;
import org.gcube.search.sru.search.adapter.client.inject.SruSearchAdapterClientModule;
import org.gcube.search.sru.search.adapter.commons.apis.SruSearchAdapterServiceAPI;
import org.gcube.search.sru.search.adapter.commons.discoverer.SruSearchAdapterDiscovererAPI;
import org.gcube.search.sru.search.adapter.commons.resources.SruSearchAdapterResource;
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

public class SruSearchAdapterClient {

	private String endpoint;
	private String resourceID;
	private final String scope;
	
	private static final Logger logger = LoggerFactory
			.getLogger(SruSearchAdapterClient.class);
	
	public static class Builder {
		private String endpoint;
		private String resourceID;
		private String scope;
		
		private final SruSearchAdapterDiscovererAPI<SruSearchAdapterResource> discoverer;
		
		public Builder(){
			this.discoverer = null;
		}
		
		@Inject
		public Builder(SruSearchAdapterDiscovererAPI<SruSearchAdapterResource> discoverer){
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
		
		public SruSearchAdapterClient build() throws SruSearchAdapterClientException {
			return new SruSearchAdapterClient(this);
		}
		
		
	}
	
	
	SruSearchAdapterClient(Builder builder) throws SruSearchAdapterClientException {
		this.endpoint = builder.endpoint;
		this.scope = builder.scope;
		this.resourceID = builder.resourceID;
		
		SruSearchAdapterDiscovererAPI<SruSearchAdapterResource> discoverer = builder.discoverer;
		
		if (discoverer == null) {
			Injector injector = Guice.createInjector(new SruSearchAdapterClientModule());
			discoverer = injector.getInstance(Key.get(new TypeLiteral<SruSearchAdapterDiscovererAPI<SruSearchAdapterResource>>(){}));
		}
		
		this.initialize(discoverer);
	}


	private void initialize(final SruSearchAdapterDiscovererAPI<SruSearchAdapterResource> discoverer) throws SruSearchAdapterClientException {
		boolean found = false;
		
		try {
			
			Map<String, Set<String>> srudbNodes = discoverer
					.discoverSruSearchAdapterNodes(this.scope, null);
			
			
			logger.info("running instances for index service : " + srudbNodes);
			
			List<String> endpoints = Lists.newArrayList(srudbNodes.keySet());
			
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					endpoints = Lists.newArrayList(this.endpoint);
				} else {
					throw new SruSearchAdapterClientException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
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
							throw new SruSearchAdapterClientException("could not initialize random client. given resourceID : " + this.resourceID + " found resourceIDs : " + resources);
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
			throw new SruSearchAdapterClientException("could not initialize random client", e);
		}
		
		if (!found)
			throw new SruSearchAdapterClientException("could not initialize random client");
	}
	
	
	
	private static SruSearchAdapterServiceAPI getSruSearchAdapterServiceProxy(String endpoint) throws SruSearchAdapterClientException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		SruSearchAdapterServiceAPI simple = null;
		
		logger.info("getting proxy from index factory service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			simple = target.proxy(SruSearchAdapterServiceAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint, e);
			throw new SruSearchAdapterClientException("Client could not connect to endpoint : " + endpoint, e);
		}
		
		logger.info("getting proxy from sru factory service...OK");
		
		return simple;
	}
	
	
	
	public String call(String operation, Float version, String recordPacking, String query, Integer maximumRecords, String recordSchema) throws SruSearchAdapterClientException {
		
		logger.info("calling call with parameters. operation : " + operation + ", version : " + version + ", recordPacking : " + recordPacking + ", query : " + query + ", maximumRecords : " + maximumRecords + ", " + recordSchema);
		logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
		
		Response response = getSruSearchAdapterServiceProxy(this.endpoint)
				.get(this.scope, this.resourceID, operation, version, recordPacking, query, maximumRecords, recordSchema);
		

		logger.info("get returned");

		try {
			ResponseUtils.checkResponse(response, "get");
		} catch (Exception e) {
			throw new SruSearchAdapterClientException(e);
		}

		String xml = response.readEntity(String.class);
		response.close();

		return xml;
	}
	
	
	public String explain() throws SruSearchAdapterClientException {
		return this.call("explain", null, null, null, null, null);
	}
	
	public String searchRetrieve(Float version, String recordPacking, String query, Integer maximumRecords, String recordSchema) throws SruSearchAdapterClientException {
		return this.call("searchRetrieve", version, recordPacking, query, maximumRecords, recordSchema);
	}
}
