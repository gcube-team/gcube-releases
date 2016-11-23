package org.gcube.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.search.commons.SearchDiscoverer;
import org.gcube.rest.search.commons.SearchDiscovererAPI;
import org.gcube.rest.search.commons.SearchServiceAPI;
import org.gcube.search.exceptions.SearchClientException;
import org.gcube.search.exceptions.SearchException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//use SearchClient2 instead
@Deprecated
public class SearchClient {
	private static final Logger logger = LoggerFactory.getLogger(SearchClient.class);
	
	private Gson gson = new Gson();
	private String scope;
	private String endpoint;
	private SearchDiscovererAPI searchDiscoverer;
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		if (this.scope != null && !this.scope.equals(scope)){
			this.searchDiscoverer = new SearchDiscoverer(new RIDiscovererISimpl());
		}
		this.scope = scope;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public SearchClient(String scope) {
		
		
		this.searchDiscoverer = new SearchDiscoverer(new RIDiscovererISimpl());
		this.scope = scope;
	}
	
	public SearchClient(Boolean b) {
		//when the RIDiscovererRRimpl ready it should be initialized here
		//for now not having scope means that discoverer will fail
	}
	
	public SearchClient() {
		//when the RIDiscovererRRimpl ready it should be initialized here
		//for now not having scope means that discoverer will fail
	}

	public void initializeClient(String endpoint) {
		this.endpoint = endpoint;
	}

	public void randomClient() throws SearchClientException {
		Set<String> ris = this.searchDiscoverer
				.discoverSearchSystemRunninInstances(this.scope);
		if (ris == null || ris.size() == 0){
			throw new SearchClientException("No search endopoints found");
		}
		
		List<String> searchServices = new ArrayList<String>(ris);
		Collections.shuffle(searchServices);
		this.endpoint = searchServices.get(0);
	}

	public String query(String query, Set<String> sids, Boolean names)
			throws SearchException {

		Response response = null;
		ResteasyClient client = null;
		try {
			ResteasyWebTarget target = null;
			SearchServiceAPI simple = null;
	
			try {
				client = new ResteasyClientBuilder().build();
				target = client.target(this.endpoint);
				simple = target.proxy(SearchServiceAPI.class);
			} catch (Exception e) {
				logger.error("Client could not connect to endpoint : " + this.endpoint, e);
				throw new SearchException("Client could not connect to endpoint : " + this.endpoint, e);
			}
			
			if (sids != null)
				response = simple
					.searchSec(scope, query, false, false, names, sids);
			else
				response = simple.search(scope, query, false, false, names);
	
			
			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				String error = response.readEntity(String.class);
				response.close();
				logger.error("query error : " + error);
				throw new SearchException("query error : " + error);
			}
	
			String json = response.readEntity(String.class);
			response.close();
	
			Map<String, String> resp = gson.fromJson(json,
					new TypeToken<Map<String, String>>() {
					}.getType());
	
			return resp.get("grslocator");
		} catch (Exception e){
			throw new SearchException("error while calling the search client", e);
		} finally {
			try {
				response.close();
			} catch (Exception e){
			}
			try {
				client.close();
			} catch (Exception e){
			}
		}
	}

	public List<Map<String, String>> queryAndRead(String query,
			Set<String> sids, Boolean names) throws SearchException {

		Response response = null;
		ResteasyClient client = null;

		try {
			ResteasyWebTarget target = null;
			SearchServiceAPI simple = null;
	
			try {
				client = new ResteasyClientBuilder().build();
				target = client.target(this.endpoint);
				simple = target.proxy(SearchServiceAPI.class);
			} catch (Exception e) {
				throw new SearchException("Client could not connect to endpoint : " + this.endpoint, e);
			}
	
	
			if (sids != null)
				response = simple.searchSec(scope, query, true, false, names, sids);
			else
				response = simple.search(scope, query, true, false, names);
	
			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				String error = response.readEntity(String.class);
				response.close();
				logger.error("query error : " + error);
				throw new SearchException("query error : " + error);
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
				throw new SearchException("could not convert response to map object", e);
			} 
			return resp;
		} catch (Exception e){
			throw new SearchException("error while calling the search client", e);
		} finally {
			try {
				response.close();
			} catch (Exception e){
			}
			try {
				client.close();
			} catch (Exception e){
			}
		}
	}


}
