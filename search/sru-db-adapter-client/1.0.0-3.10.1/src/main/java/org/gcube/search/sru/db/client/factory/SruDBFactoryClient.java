package org.gcube.search.sru.db.client.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.gcube.search.sru.db.client.exception.SruDBClientException;
import org.gcube.search.sru.db.client.inject.SruDBClientModule;
import org.gcube.search.sru.db.common.apis.SruDBServiceFactoryAPI;
import org.gcube.search.sru.db.common.discoverer.SruDBDiscovererAPI;
import org.gcube.search.sru.db.common.resources.ExplainInfo;
import org.gcube.search.sru.db.common.resources.SruDBResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class SruDBFactoryClient {
	private static Gson gson = new Gson();

	private String endpoint;
	
	private static final Logger logger = LoggerFactory.getLogger(SruDBFactoryClient.class);
	
	public String getEndpoint() {
		return this.endpoint;
	}

	public static class Builder {
		private String endpoint;
		private String scope;
		private boolean skipInitialize = false;

		private final SruDBDiscovererAPI<SruDBResource> discoverer;

		public Builder() {
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
		
		public Builder scope(String scope){
			this.scope = scope;
			return this;
		}
		
		public Builder skipInitialize(boolean skipInitialize){
			this.skipInitialize  = skipInitialize;
			return this;
		}
		
		public SruDBFactoryClient build() throws SruDBClientException {
			return new SruDBFactoryClient(this);
		}
	}
	
	SruDBFactoryClient(Builder builder) throws SruDBClientException  {
		this.endpoint = builder.endpoint;
		
		SruDBDiscovererAPI<SruDBResource> discoverer = builder.discoverer;
		
		if (discoverer == null) {
			Injector injector = Guice.createInjector(new SruDBClientModule());
			discoverer = injector.getInstance(Key.get(new TypeLiteral<SruDBDiscovererAPI<SruDBResource>>(){}));
		}
		
		if (builder.skipInitialize == true && builder.scope != null && this.endpoint != null){
			logger.info("requested to skip the initialize part");
		} else {
			this.initialize(discoverer, builder.scope);
		}
	}

	private void initialize(final SruDBDiscovererAPI<SruDBResource> discoverer,
			final String scope) throws SruDBClientException  {
		
		try {
			Set<String> srudbNodes = discoverer.discoverSruDBNodeRunningInstances(scope);
	
			List<String> endpoints = Lists.newArrayList(srudbNodes);
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					endpoints = Lists.newArrayList(this.endpoint);
				} else {
					throw new SruDBClientException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
				}
			} else {
				Collections.shuffle(endpoints);
			}
			
			this.endpoint = endpoints.get(0);
			
			logger.info("Initialized at : " + this.endpoint);
		} catch (Exception e) {
			logger.error("could not initialize random client", e);
			throw new SruDBClientException("could not initialize random client", e);
		}
		
	}
	
	
	public String createResource(SruDBResource resource, String scope) throws SruDBClientException{
		
		if (scope != null)
			resource.setScope(scope);
		
		String json = resource.toJSON();
		
		logger.info("calling create resource with json params : " + json);
		
		Response response = null;
		
		response = getSruServiceFactoryProxy(this.endpoint).createResourceREST(scope, json);
		
		logger.info("createResource returned");
		
		if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
			String error = response.readEntity(String.class);
			response.close();
			throw new SruDBClientException("resource could not be created : " + error);
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
	
	private static SruDBServiceFactoryAPI getSruServiceFactoryProxy(String endpoint) throws SruDBClientException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		SruDBServiceFactoryAPI simple = null;
		
		logger.info("getting proxy from index factory service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			simple = target.proxy(SruDBServiceFactoryAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint, e);
			throw new SruDBClientException("Client could not connect to endpoint : " + endpoint, e);
		}
		
		logger.info("getting proxy from index factory service...OK");
		
		return simple;
	}
	
	
	
	public static class ResourceBuilder {
		private String serverHost;
		private Integer serverPort;
		private String databaseName;
		private String databaseType;
		
		private String databaseUsername;
		private String databasePassword;
		
		private String databaseTitle;
		private String databaseDescription;
		
		private String scope;
		
		
		private Map<String, ArrayList<String>> tables;
		
		private String schemaName;
		private String schemaID;
		private String recordPacking;
		private String defaultTable;
		
		private Map<String, String> fieldsMapping;

		public ResourceBuilder scope(String scope){
			this.scope = scope;
			return this;
		}
		
		public ResourceBuilder schemaID(String schemaID){
			this.schemaID = schemaID;
			return this;
		}
		
		public ResourceBuilder tables(Map<String, ArrayList<String>> tables){
			this.tables = tables;
			return this;
		}
		
		public ResourceBuilder fieldsMapping(Map<String, String> fieldsMapping){
			this.fieldsMapping = fieldsMapping;
			return this;
		}
		
		public ResourceBuilder schemaName(String schemaName){
			this.schemaName = schemaName;
			return this;
		}
		
		public ResourceBuilder recordPacking(String recordPacking){
			this.recordPacking = recordPacking;
			return this;
		}
		
		
		public ResourceBuilder serverHost(String serverHost){
			this.serverHost = serverHost;
			return this;
		}
		
		public ResourceBuilder serverPort(Integer serverPort){
			this.serverPort = serverPort;
			return this;
		}
		
		public ResourceBuilder databaseName(String databaseName){
			this.databaseName = databaseName;
			return this;
		}
		
		public ResourceBuilder databaseTitle(String databaseTitle){
			this.databaseTitle = databaseTitle;
			return this;
		}
		
		public ResourceBuilder databaseType(String databaseType){
			this.databaseType = databaseType;
			return this;
		}
		
		public ResourceBuilder databaseDescription(String databaseDescription){
			this.databaseDescription = databaseDescription;
			return this;
		}
		
		public ResourceBuilder databaseUsername(String databaseUsername){
			this.databaseUsername = databaseUsername;
			return this;
		}
		
		public ResourceBuilder databasePassword(String databasePassword){
			this.databasePassword = databasePassword;
			return this;
		}
		
		public ResourceBuilder defaultTable(String defaultTable){
			this.defaultTable = defaultTable;
			return this;
		}
		
		
		public SruDBResource build(){
			SruDBResource resource = new SruDBResource();
			ExplainInfo explainInfo = new ExplainInfo();
			
			//table and fields
			
			
			Map<String, String> indexSets = Maps.newHashMap();
			indexSets.put("info:srw/cql-context-set/1/cql-v1.1", "cql");
			indexSets.put("info:srw/cql-context-set/1/dc-v1.1", "oai_dc");
			
			explainInfo.setIndexSets(indexSets);
			

			Set<String> allFields = Sets.newHashSet();			
			for (Entry<String, ArrayList<String>> entry : tables.entrySet()) {
				allFields.addAll(entry.getValue());
			}
			
			allFields.addAll(fieldsMapping.keySet());
			
			allFields.retainAll(DC_FIELDS);
			
			Map<String, ArrayList<String>> collectionsAndFields = Maps.newHashMap();
			collectionsAndFields.put("oai_dc", Lists.newArrayList(allFields));
			collectionsAndFields.put("cql", Lists.newArrayList("allIndexes"));
			
			explainInfo.setIndexInfo(collectionsAndFields);
			
			
			if (this.schemaID == null)
				explainInfo.setSchemaID("info:srw/schema/1/dc-v1.1");
			else
				explainInfo.setSchemaID(this.schemaID);
			
			explainInfo.setDefaultTable(this.defaultTable);
			
			explainInfo.setSchemaName(this.schemaName);
			explainInfo.setRecordSchema("http://explain.z3950.org/dtd/2.0/");
			explainInfo.setRecordPacking(this.recordPacking);
			
			resource.setFieldsMapping(this.fieldsMapping);
			
			
			resource.setDbName(this.databaseName);
			resource.setDbType(this.databaseType);
			resource.setDbTitle(this.databaseTitle);
			resource.setDbDescription(this.databaseDescription);
			
			resource.setHostname(this.serverHost);
			resource.setPort(this.serverPort);
			resource.setUsername(this.databaseUsername);
			resource.setPassword(this.databasePassword);
			
			resource.setScope(this.scope);
			
			resource.setExplainInfo(explainInfo);
			
			return resource;
		} 
		
	}
	
	static final List<String> DC_FIELDS = Lists.newArrayList("title", "creator",
			"subject", "description", "publisher", "contributor", "date",
			"type", "format", "identifier", "source", "language", "relation",
			"coverage", "rights");
}
