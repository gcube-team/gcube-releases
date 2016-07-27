package org.gcube.rest.index.client;

import static org.gcube.rest.index.client.helpers.ResponseUtils.checkResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.index.client.exceptions.IndexException;
import org.gcube.rest.index.client.inject.IndexClientModule;
import org.gcube.rest.index.common.apis.IndexServiceAPI;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.entities.ClusterResponse;
import org.gcube.rest.index.common.helpers.ResultReader;
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

public class IndexClient {
	private static Gson gson = new Gson();

	private String endpoint;
	private String resourceID;

	private final String scope;
	private final String clusterID;
	private final String indexID;
	
	private final String collectionID;
	
	private static final Logger logger = LoggerFactory
			.getLogger(IndexClient.class);
	
	
	public static class Builder {
		private String endpoint;
		private String resourceID;

		private String scope;
		private String clusterID;
		private String indexID;
		
		private String collectionID;
		
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
		
		public Builder resourceID(String resourceID){
			this.resourceID = resourceID;
			return this;
		}
		
		public Builder scope(String scope){
			this.scope = scope;
			return this;
		}
		
		public Builder clusterID(String clusterID){
			this.clusterID = clusterID;
			return this;
		}
		
		public Builder indexID(String indexID){
			this.indexID = indexID;
			return this;
		}
		
		public Builder collectionID(String collectionID){
			this.collectionID = collectionID;
			return this;
		}
		
		public IndexClient build() throws IndexException{
			return new IndexClient(this);
		}
	}
	
	public IndexClient(Builder builder) throws IndexException {
		this.indexID = builder.indexID;
		this.clusterID = builder.clusterID;
		this.endpoint = builder.endpoint;
		this.scope = builder.scope;
		this.resourceID = builder.resourceID;
		this.collectionID = builder.collectionID;
		
		IndexDiscovererAPI<IndexResource> indexDiscoverer = builder.indexDiscoverer;
		if (indexDiscoverer == null) {
			Injector injector = Guice.createInjector(new IndexClientModule());
			indexDiscoverer = injector.getInstance(Key.get(new TypeLiteral<IndexDiscovererAPI<IndexResource>>(){}));
		}
			
		this.intialize(indexDiscoverer);
	}
	
	private final void intialize(IndexDiscovererAPI<IndexResource> indexDiscoverer) throws IndexException {
		
		boolean found = false;
		
		try {
			Map<String, Set<String>> fulltextIndexNodes = indexDiscoverer
					.discoverFulltextIndexNodes(this.clusterID, this.indexID, this.collectionID, this.scope);
			
			logger.info("running instances for index service : " + fulltextIndexNodes);
	
			List<String> endpoints = Lists.newArrayList(fulltextIndexNodes.keySet());
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					endpoints = Lists.newArrayList(this.endpoint);
				} else {
					throw new IndexException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
				}
			} else {
				Collections.shuffle(endpoints);
			}
			
			
			for (String endpoint : endpoints) {
			
				if (fulltextIndexNodes.get(endpoint) != null &&
						fulltextIndexNodes.get(endpoint).size() > 0) {
					List<String> resources = Lists.newArrayList(fulltextIndexNodes.get(endpoint));
					
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
			throw new IndexException("could not initialize random client", e);
		}
		
		if (!found) {
			if (this.resourceID != null)
				throw new IndexException("could not initialize random client. given resourceID : " + this.resourceID);
			else
				throw new IndexException("could not initialize random client");
			
			
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
	
	public String getClusterID() {
		return this.clusterID;
	}
	
	public String getIndexID() {
		return this.indexID;
	}
	
	private static IndexServiceAPI getFullTextIndexServiceProxy(
			String endpoint) throws IndexException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		IndexServiceAPI simple = null;

		logger.info("getting proxy from index service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			
			simple = target.proxy(IndexServiceAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint,
					e);
			throw new IndexException("Client could not connect to endpoint : "
					+ endpoint, e);
		}

		logger.info("getting proxy from index service...OK");
		
		return simple;
	}
	
	private static ResteasyWebTarget getTarget(
			String endpoint) throws IndexException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;

		logger.info("getting proxy from index service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint,
					e);
			throw new IndexException("Client could not connect to endpoint : "
					+ endpoint, e);
		}

		logger.info("getting proxy from index service...OK");
		
		return target;
	}

	public boolean feedLocator(String resultSetLocation, String indexName,
			Boolean activate, Set<String> sids) throws IndexException {
	
		try {
			logger.info("calling feedLocator with parameters. resultSetLocation : " + resultSetLocation + ", indexName : " + indexName + ", activate : " + activate + ", sids : " + sids);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint)
					.feedLocator(this.scope, this.resourceID, resultSetLocation, indexName, activate, sids, false);
	
			logger.info("feedLocator returned");
			
			checkResponse(response, "feedLocator");
	
			return (response.getStatus() == Response.Status.OK.getStatusCode());
		
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}
	
	public boolean feedLocatorSync(String resultSetLocation, String indexName,
			Boolean activate, Set<String> sids) throws IndexException {
		
		try {
			logger.info("calling feedLocatorSync with parameters. resultSetLocation : " + resultSetLocation + ", indexName : " + indexName + ", activate : " + activate + ", sids : " + sids);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint)
					.feedLocator(this.scope, this.resourceID, resultSetLocation, indexName, activate, sids, true);
	
			logger.info("feedLocatorSync returned");
			
			checkResponse(response, "feedLocatorSync");
	
			return (response.getStatus() == Response.Status.OK.getStatusCode());
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}
	
	public boolean flush() throws IndexException {
		try {
			logger.info("calling flush");
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint)
					.flush(this.scope, this.resourceID);
	
			logger.info("flush returned");
			
			checkResponse(response, "flush");
	
			return (response.getStatus() == Response.Status.OK.getStatusCode());
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}
	
	
	public List<ClusterResponse> clustering(String queryString, String queryHint) throws IndexException {
		return this.clustering(queryString, queryHint, null, 10, "ObjectID", Lists.newArrayList("title"), Lists.newArrayList("description"), Lists.newArrayList("gDocCollectionLang"), null, null);
	}
	
	public List<ClusterResponse> clustering(String queryString, String queryHint, Set<String> sids, Integer numberOfClusters, String urlField, List<String> titleFields, List<String> contentFields, List<String> languageFields) throws IndexException {
		return this.clustering(queryString, queryHint, sids, numberOfClusters, urlField, titleFields, contentFields, languageFields, null, null);
	}
	
	public List<ClusterResponse> clustering(String queryString, String queryHint, Set<String> sids, Integer numberOfClusters, String urlField, List<String> titleFields, List<String> contentFields, List<String> languageFields, String algorithm, Integer searchHits) throws IndexException {
		try {
			logger.info("calling cluster with parameters. queryString : " + queryString + ", sids : " + sids + ", numberOfClusters : " + numberOfClusters + ", urlField : " + urlField + ", titleField : " + titleFields + ", contentField : " + contentFields + ", languageField : " + languageFields + ", algorithm : " + algorithm + ", searchHits : " + searchHits);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint)
					.clustering(this.scope, this.resourceID, queryString, queryHint, numberOfClusters, urlField, titleFields, contentFields, languageFields, algorithm, searchHits, sids, false, false);
			
			logger.info("cluster returned");
	
			checkResponse(response, "cluster");
	
			String json = response.readEntity(String.class);
			response.close();
	
			List<ClusterResponse> resp = gson.fromJson(json,
					new TypeToken<List<ClusterResponse>>() {
					}.getType());
	
			return resp;
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}
	
	
	public Map<String, Integer> frequentTerms(String queryString, Set<String> sids) throws IndexException{
		return this.frequentTerms(queryString, sids, 10, true);
	}
	
	public Map<String, Integer> frequentTerms(String queryString, Set<String> sids, Boolean useRR) throws IndexException{
		return this.frequentTerms(queryString, sids, 10, useRR);
	}
	
	public Map<String, Integer> frequentTerms(String queryString, Set<String> sids, Integer maxTerms, Boolean useRR) throws IndexException {
		try {
			logger.info("calling frequentTerms with parameters. queryString : " + queryString + ", sids : " + sids + ", maxTerms : " + maxTerms + ", useRR : " + useRR);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint)
					.frequentTerms(this.scope, this.resourceID, queryString, maxTerms, sids, useRR, false);
			
			logger.info("frequentTerms returned");
	
			checkResponse(response, "frequentTerms");
	
			String json = response.readEntity(String.class);
			response.close();
	
			Map<String, Integer> resp = gson.fromJson(json,
					new TypeToken<Map<String, Integer>>() {
					}.getType());
	
			return resp;
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}
	

	public String query(String queryString, Set<String> sids) throws IndexException{
		return this.query(queryString, sids, 0, -1, true);
	}
	
	public String query(String queryString, Set<String> sids, Boolean useRR) throws IndexException{
		return this.query(queryString, sids, 0, -1, useRR);
	}
	
	public String query(String queryString, Set<String> sids, Integer from, Integer count) throws IndexException {
		return this.query(queryString, sids, from, count, true);
	}
	
	public String query(String queryString, Set<String> sids, Integer from, Integer count, Boolean useRR) throws IndexException {
		try {
			logger.info("calling query with parameters. queryString : " + queryString + ", sids : " + sids + ", from : " + from + ", count : " + count + ", useRR : " + useRR);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
//			Response response = getFullTextIndexServiceProxy(this.endpoint)
//					.query(this.scope, this.resourceID, queryString, from, count, sids, useRR, false, false, false, "application/x-www-form-urlencode; charset=utf-8");
			
			
			ResteasyWebTarget t = getTarget(endpoint + "/" + resourceID + "/query");
			
			Form form = new Form()
				.param("queryString", queryString);
			
			if (from != null)
				form.param("from", String.valueOf(from));
			if (count != null)
				form.param("count", String.valueOf(count));
			
			if (sids != null){
				for (String sid : sids)
					form.param("sids", String.valueOf(sid));
			}
			
			if (useRR != null){
				form.param("useRR", String.valueOf(useRR));
			}
			
			form.param("result", "false");
			form.param("stream", "false");
			form.param("pretty", "false");
			
			MediaType mt = new MediaType("application", "x-www-form-urlencoded", "UTF-8");
			Variant v = new Variant(mt, Locale.ENGLISH, "UTF-8");
			
			Response response = t.request()
					.header(ResourceAwareServiceConstants.SCOPE_HEADER, this.scope)
					.post(Entity.entity(form, v ));
			
			logger.info("query returned");
	
			checkResponse(response, "query");
	
			String json = response.readEntity(String.class);
			response.close();
	
			Map<String, String> resp = gson.fromJson(json,
					new TypeToken<Map<String, String>>() {
					}.getType());
	
			return resp.get("grslocator");
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}

	public List<Map<String, String>> queryAndReadClientSide(String queryString,
			Set<String> sids) throws IndexException {
		return this.queryAndReadClientSide(queryString, sids, true);
	}
	
	public List<Map<String, String>> queryAndReadClientSide(String queryString,
			Set<String> sids, Boolean useRR) throws IndexException {
		return this.queryAndReadClientSide(queryString, sids, 0, -1, useRR);
	} 
	
	public List<Map<String, String>> queryAndReadClientSide(String queryString,
			Set<String> sids, Integer from, Integer count) throws IndexException {
		
		return this.queryAndReadClientSide(queryString, sids, from, count, true);
	}
			
	public List<Map<String, String>> queryAndReadClientSide(String queryString,
			Set<String> sids, Integer from, Integer count, Boolean useRR) throws IndexException {
		try {
			logger.info("calling queryAndReadClientSide with parameters. queryString : " + queryString + ", sids : " + sids + ", from : " + from + ", count : " + count + ", useRR : " + useRR);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			String grsLocator = this.query(queryString, sids, from, count, useRR);
			
			logger.info("queryAndReadClientSide returned : " + grsLocator);
			
			try {
				List<Map<String, String>> results = ResultReader.resultSetToRecords(grsLocator);
				
				return results;
			} catch (Exception e) {
				logger.error("could not read results from the grs2 locator : " + grsLocator, e);
				throw new IndexException("could not read results from the grs2 locator : " + grsLocator, e);
			}
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}

	public List<Map<String, String>> queryAndRead(String queryString,
			Set<String> sids, Boolean useRR) throws IndexException {
		return this.queryAndRead(queryString, sids, 0, -1, useRR);
	}
	
	public List<Map<String, String>> queryAndRead(String queryString,
			Set<String> sids) throws IndexException {
		return this.queryAndRead(queryString, sids, true);
	}
	
	public List<Map<String, String>> queryAndRead(String queryString,
			Set<String> sids, Integer from, Integer count) throws IndexException {
		return this.queryAndRead(queryString, sids, from , count, true);
	}
	public List<Map<String, String>> queryAndRead(String queryString,
			Set<String> sids, Integer from, Integer count, Boolean useRR) throws IndexException {

		try {
			logger.info("calling queryAndRead with parameters. queryString : " + queryString + ", sids : " + sids + ", from : " + from + ", count : " + count + ", useRR : " + useRR);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
//			Response response = getFullTextIndexServiceProxy(this.endpoint).query(this.scope, 
//					this.resourceID,
//					queryString,  from, count, sids, useRR, true, false, false,
//					"application/x-www-form-urlencode; charset=utf-8");
			
			ResteasyWebTarget t = getTarget(endpoint + "/" + resourceID + "/query");
			
			Form form = new Form()
				.param("queryString", queryString);
			
			if (from != null)
				form.param("from", String.valueOf(from));
			if (count != null)
				form.param("count", String.valueOf(count));
			
			if (sids != null){
				for (String sid : sids)
					form.param("sids", String.valueOf(sid));
			}
			
			if (useRR != null){
				form.param("useRR", String.valueOf(useRR));
			}
			
			form.param("result", "true");
			form.param("stream", "false");
			form.param("pretty", "false");
			
			MediaType mt = new MediaType("application", "x-www-form-urlencoded", "UTF-8");
			Variant v = new Variant(mt, Locale.ENGLISH, "UTF-8");
			
			Response response = t.request()
					.header(ResourceAwareServiceConstants.SCOPE_HEADER, this.scope)
					.post(Entity.entity(form, v ));
			
	
			logger.info("queryAndRead returned");
			
			checkResponse(response, "queryAndRead");
	
			String json = response.readEntity(String.class);
			response.close();
	
			try {
				List<Map<String, String>> resp = gson.fromJson(json,
						new TypeToken<List<Map<String, String>>>() {
						}.getType());
				return resp;
			} catch (Exception e) {
				logger.error("Unable to convert response to list of maps of strings : " + json);
				throw new IndexException("Unable to convert response to list of maps of strings " + json, e);
			}
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}

	}

	public Boolean refresh() throws IndexException {
		
		try {
		
			logger.info("calling refresh");
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint).refresh(this.scope, this.resourceID);
	
			logger.info("refresh returned");
			
			checkResponse(response, "refresh");
			
			int status = response.getStatus();
			response.close();
			
			return Boolean.valueOf(status == Response.Status.OK.getStatusCode());
			
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}
	
	public Boolean setCollectionsAndFields(List<String> collections, List<String> fields) throws IndexException {
		
		try {
			
			logger.info("calling setCollectionsAndFields with parameters. collections : " + collections + ", fields : " + fields);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint).setCollectionsAndFields(this.scope, this.resourceID, collections, fields);
	
			logger.info("setCollectionsAndFields returned");
			
			checkResponse(response, "setCollectionsAndFields");
	
			int status = response.getStatus();
			response.close();
			
			return Boolean.valueOf(status == Response.Status.OK.getStatusCode());
			
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}

	public Boolean shutdown(Boolean delete) throws IndexException {
		try {
			
			logger.info("calling shutdown with parameters. delete : " + delete);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint).shutdown(this.scope, this.resourceID,
					delete);
			
			logger.info("shutdown returned");
	
			checkResponse(response, "shutdown");
	
			int status = response.getStatus();
			response.close();
			
			return Boolean.valueOf(status == Response.Status.OK.getStatusCode());
		
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}

	public Boolean destroyCluster() throws IndexException {
		try {
			logger.info("calling destroyCluster");
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint)
					.destroyCluster(this.scope, this.resourceID);
	
			logger.info("destroyCluster returned");
			
			checkResponse(response, "destroyCluster");
	
			int status = response.getStatus();
			response.close();
			
			return Boolean.valueOf(status == Response.Status.OK.getStatusCode());
			
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}
	
	public Boolean destroy() throws IndexException {
		try {
			logger.info("calling destroy");
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint)
					.destroyResourceREST(this.scope, this.resourceID);
	
			logger.info("destroy returned");
			
			checkResponse(response, "destroy");
	
			int status = response.getStatus();
			response.close();
			
			return Boolean.valueOf(status == Response.Status.OK.getStatusCode());
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}

	public Boolean rebuildMetaIndex() throws IndexException {
		try {
			logger.info("calling rebuildMetaIndex");
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint).rebuildMetaIndex(this.scope,
					this.resourceID);
			
			logger.info("rebuildMetaIndex returned");
	
			checkResponse(response, "rebuildMetaIndex");
	
			int status = response.getStatus();
			response.close();
			
			return Boolean.valueOf(status == Response.Status.OK.getStatusCode());
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}

	public Boolean deleteIndex(String indexName) throws IndexException {
		try {
			logger.info("calling deleteIndex with parameters. indexName : " + indexName);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint).deleteIndex(this.scope,
					this.resourceID, indexName);
			
			logger.info("deleteIndex returned");
	
			checkResponse(response, "deleteIndex");
	
			int status = response.getStatus();
			response.close();
			
			return Boolean.valueOf(status == Response.Status.OK.getStatusCode());
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}
	
	
	public Boolean deleteDocuments(List<String> documentIDs) throws IndexException {
		try {
			logger.info("calling deleteDocuments with parameters. documentIDs : " + documentIDs);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint).deleteDocuments(this.scope,
					this.resourceID, documentIDs);
			
			logger.info("deleteDocuments returned");
	
			checkResponse(response, "deleteDocuments");
	
			int status = response.getStatus();
			response.close();
			
			return Boolean.valueOf(status == Response.Status.OK.getStatusCode());
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}

	public Set<String> collectionsOfIndex(String indexName) throws IndexException {
		try {
			logger.info("calling collectionsOfIndex with parameters. indexName : " + indexName);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint).collectionsOfIndex(this.scope,
					this.resourceID, indexName);
	
			logger.info("collectionsOfIndex returned");
			
			checkResponse(response, "collectionsOfIndex");
	
			String json = response.readEntity(String.class);
			response.close();
			
			try {
				Map<String, Set<String>> resp = gson.fromJson(json,
						new TypeToken<Map<String, Set<String>>>() {
				}.getType());
				
				return resp.get("response");
			} catch (Exception e) {
				logger.error("Unable to convert response to map strings : " + json);
				throw new IndexException("Unable to convert response to list of maps of strings " + json, e);
			}
			
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}

	public Set<String> indicesOfCollection(String collectionID)
			throws IndexException {
		try {
			logger.info("calling indicesOfCollection with parameters. collectionID : " + collectionID);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint).indicesOfCollection(this.scope,
					this.resourceID, collectionID);
	
			logger.info("indicesOfCollection returned");
			
			checkResponse(response, "indicesOfCollection");
	
			String json = response.readEntity(String.class);
			response.close();
	
			try {
				Map<String, Set<String>> resp = gson.fromJson(json,
						new TypeToken<Map<String, Set<String>>>() {
				}.getType());
				
				return resp.get("response");
			} catch (Exception e) {
				logger.error("Unable to convert response to map strings : " + json);
				throw new IndexException("Unable to convert response to list of maps of strings " + json, e);
			}
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}

	public Integer collectionCount(String collectionID) throws IndexException {
		try {
			logger.info("calling collectionCount with parameters. collectionID : " + collectionID);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint).collectionCount(this.scope,
					this.resourceID, collectionID);
	
			checkResponse(response, "collectionCount");
			
			logger.info("collectionCount returned");
	
			String json = response.readEntity(String.class);
			response.close();
			
			try {
				Map<String, Integer> resp = gson.fromJson(json,
						new TypeToken<Map<String, Integer>>() {
				}.getType());
				
				return resp.get("response");
			} catch (Exception e) {
				logger.error("Unable to convert response to map strings : " + json);
				throw new IndexException("Unable to convert response to list of maps of strings " + json, e);
			}
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
		
	}
	

	public Boolean activateIndex(String indexName) throws IndexException {
		
		try {
			logger.info("calling activateIndex with parameters. indexName : " + indexName);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint).activateIndex(this.scope, this.resourceID,
					indexName);
	
			logger.info("activateIndex returned");
	
			checkResponse(response, "activateIndex");
			
	
			int status = response.getStatus();
			response.close();
			
			return Boolean.valueOf(status == Response.Status.OK.getStatusCode());
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}
	
	public Boolean deactivateIndex(String indexName) throws IndexException {
		try {
			logger.info("calling deactivateIndex with parameters. indexName : " + indexName);
			logger.info(" ~> endpoint : " + this.endpoint + ", resourceID : " + this.resourceID + ", scope : " + this.scope);
			
			Response response = getFullTextIndexServiceProxy(this.endpoint)
					.deactivateIndex(this.scope, this.resourceID, indexName);
	
			logger.info("deactivateIndex returned");
	
			checkResponse(response, "deactivateIndex");
			
	
			int status = response.getStatus();
			response.close();
			
			return Boolean.valueOf(status == Response.Status.OK.getStatusCode());
		} catch (Exception e){
			throw new IndexException("error while calling the index client", e);
		}
	}

}
