package org.gcube.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.gcube.rest.commons.helpers.ResponseUtils;
import org.gcube.rest.search.commons.SearchDiscovererAPI;
import org.gcube.rest.search.commons.SearchServiceAPI;
import org.gcube.search.exceptions.SearchClientException;
import org.gcube.search.inject.SearchClientModule;
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

public class SearchClient2 {
	private static final Logger logger = LoggerFactory.getLogger(SearchClient2.class);
	
	private static Gson gson = new Gson();
	
	private String scope;
	private String endpoint;
	
	public static class Builder {
		private String scope;
		private String endpoint;
		private final SearchDiscovererAPI discoverer;
		
		public Builder(SearchDiscovererAPI discoverer){
			this.discoverer = discoverer;
		}
		
		public Builder(){
			this.discoverer = null;
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
		
		public SearchClient2 build() throws SearchClientException{
			return new SearchClient2(this);
		}
	}
	
	public String getScope() {
		return scope;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public SearchClient2(Builder builder) throws SearchClientException {
		this.endpoint = builder.endpoint;
		this.scope = builder.scope;
		
		SearchDiscovererAPI discoverer = builder.discoverer;
		if (discoverer == null){
			Injector injector = Guice.createInjector(new SearchClientModule());
			discoverer = injector.getInstance(SearchDiscovererAPI.class);
		}
		
		this.initialize(discoverer);
	}
	
	private final void initialize(SearchDiscovererAPI discoverer) throws SearchClientException{
		boolean found = false;
		
		try {
			Set<String> endpoints = discoverer
					.discoverSearchSystemRunninInstances(this.scope);
			
			logger.info("running instances for searcg service : " + endpoints);
			
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					found = true;
				} else {
					throw new SearchClientException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
				}
			} else {
				List<String> endpointList = Lists.newArrayList(endpoints);
				
				Collections.shuffle(endpointList);
				
				this.endpoint = endpointList.get(0);
				
				found = true;
			}
			
			
		} catch (Exception e) {
			logger.error("could not initialize random client", e);
			throw new SearchClientException("could not initialize random client", e);
		}
		
		if (!found)
			throw new SearchClientException("could not initialize random client");
		
		
		
		Set<String> ris = discoverer
				.discoverSearchSystemRunninInstances(this.scope);
		if (ris == null || ris.size() == 0){
			throw new SearchClientException("No search endopoints found");
		}
		
		List<String> searchServices = new ArrayList<String>(ris);
		Collections.shuffle(searchServices);
		this.endpoint = searchServices.get(0);
	}


	public String query(String query, Set<String> sids, Boolean names)
			throws SearchClientException {

		Response response = null;

		if (sids != null)
			response = getSearchServiceProxy(this.endpoint)
				.searchSec(scope, query, false, false, names, sids);
		else
			response = getSearchServiceProxy(this.endpoint).search(scope, query, false, false, names);

		
		try {
			ResponseUtils.checkResponse(response, "query");
		} catch (Exception e) {
			throw new SearchClientException(e);
		}

		String json = response.readEntity(String.class);
		response.close();

		Map<String, String> resp = gson.fromJson(json,
				new TypeToken<Map<String, String>>() {
				}.getType());

		return resp.get("grslocator");
	}

	public List<Map<String, String>> queryAndRead(String query,
			Set<String> sids, Boolean names) throws SearchClientException {

		Response response = null;

		if (sids != null)
			response = getSearchServiceProxy(this.endpoint).searchSec(scope, query, true, false, names, sids);
		else
			response = getSearchServiceProxy(this.endpoint).search(scope, query, true, false, names);

		try {
			ResponseUtils.checkResponse(response, "queryAndRead");
		} catch (Exception e) {
			throw new SearchClientException(e);
		}

		String json = response.readEntity(String.class);
		response.close();

		List<Map<String, String>> resp = null;
		try {
			resp = gson.fromJson(json,
					new TypeToken<List<Map<String, String>>>() {
					}.getType());
		} catch (Exception e) {
			logger.error("could not convert response to map object", e);
			throw new SearchClientException("could not convert response to map object", e);
		} 
		return resp;
	}
	
	
	
	public Map<String, String> getCollections()
			throws SearchClientException {


		Response response = getSearchServiceProxy(this.endpoint).collections(scope);
		
		try {
			ResponseUtils.checkResponse(response, "getCollections");
		} catch (Exception e) {
			throw new SearchClientException(e);
		}
		
		String json = response.readEntity(String.class);
		response.close();
		//client.close();

		Map<String, String> resp = gson.fromJson(json,
				new TypeToken<Map<String, String>>() {
				}.getType());

		return resp;
	}
	
	public Map<String, String> getCollectionsTypes()
			throws SearchClientException {


		Response response = getSearchServiceProxy(this.endpoint).collectionsTypes(scope);
		
		try {
			ResponseUtils.checkResponse(response, "getCollectionsTypes");
		} catch (Exception e) {
			throw new SearchClientException(e);
		}
		
		String json = response.readEntity(String.class);
		response.close();
		//client.close();

		Map<String, String> resp = gson.fromJson(json,
				new TypeToken<Map<String, String>>() {
				}.getType());

		return resp;
	}
	
	public Map<String, List<String>> getSearchableFields()
			throws SearchClientException {

		Response response = getSearchServiceProxy(this.endpoint).searchableFields(scope);
		
		try {
			ResponseUtils.checkResponse(response, "getSearchableFields");
		} catch (Exception e) {
			throw new SearchClientException(e);
		}
		
		String json = response.readEntity(String.class);
		response.close();

		Map<String, List<String>> resp = gson.fromJson(json,
				new TypeToken<Map<String, List<String>>>() {
				}.getType());

		return resp;
	}
	
	public Map<String, List<String>> getPresentableFields()
			throws SearchClientException {

		Response response = getSearchServiceProxy(this.endpoint).presentableFields(scope);
		
		try {
			ResponseUtils.checkResponse(response, "getPresentableFields");
		} catch (Exception e) {
			throw new SearchClientException(e);
		}
		
		String json = response.readEntity(String.class);
		response.close();

		Map<String, List<String>> resp = gson.fromJson(json,
				new TypeToken<Map<String, List<String>>>() {
				}.getType());

		return resp;
	}
	
	public Map<String, String> getFieldsMapping()
			throws SearchClientException {

		Response response = getSearchServiceProxy(this.endpoint).fieldsMapping(scope);
		
		try {
			ResponseUtils.checkResponse(response, "getFieldsMapping");
		} catch (Exception e) {
			throw new SearchClientException(e);
		}
		
		String json = response.readEntity(String.class);
		response.close();

		Map<String, String> resp = gson.fromJson(json,
				new TypeToken<Map<String, String>>() {
				}.getType());

		return resp;
	}
	
	private static SearchServiceAPI getSearchServiceProxy(String endpoint) throws SearchClientException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		SearchServiceAPI simple = null;
		
		logger.info("getting proxy from search service...");
		try {
			client = new ResteasyClientBuilder()
//				.socketTimeout(100, TimeUnit.MILLISECONDS)
//				.establishConnectionTimeout(100, TimeUnit.MILLISECONDS)
			.build();
			target = client.target(endpoint);
			simple = target.proxy(SearchServiceAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint, e);
			throw new SearchClientException("Client could not connect to endpoint : " + endpoint, e);
		}
		
		logger.info("getting proxy from search service...OK");
		
		return simple;
	}


}
