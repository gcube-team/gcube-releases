package org.gcube.search.sru.consumer.client.factory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.helpers.XPathEvaluator;
import org.gcube.search.sru.consumer.client.exception.SruConsumerClientException;
import org.gcube.search.sru.consumer.client.inject.SruConsumerClientModule;
import org.gcube.search.sru.consumer.common.apis.SruConsumerServiceFactoryAPI;
import org.gcube.search.sru.consumer.common.discoverer.SruConsumerDiscovererAPI;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource.DescriptionDocument;
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

public class SruConsumerFactoryClient {
	private static Gson gson = new Gson();

	private String endpoint;
	
	private static final Logger logger = LoggerFactory.getLogger(SruConsumerFactoryClient.class);
	
	public String getEndpoint() {
		return this.endpoint;
	}

	public static class Builder {
		private String endpoint;
		private String scope;
		private boolean skipInitialize = false;

		private final SruConsumerDiscovererAPI<SruConsumerResource> discoverer;

		public Builder() {
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
		
		public Builder scope(String scope){
			this.scope = scope;
			return this;
		}
		
		public Builder skipInitialize(boolean skipInitialize){
			this.skipInitialize  = skipInitialize;
			return this;
		}
		
		public SruConsumerFactoryClient build() throws SruConsumerClientException {
			return new SruConsumerFactoryClient(this);
		}
	}
	
	SruConsumerFactoryClient(Builder builder) throws SruConsumerClientException  {
		this.endpoint = builder.endpoint;
		
		SruConsumerDiscovererAPI<SruConsumerResource> discoverer = builder.discoverer;
		
		if (discoverer == null) {
			Injector injector = Guice.createInjector(new SruConsumerClientModule());
			discoverer = injector.getInstance(Key.get(new TypeLiteral<SruConsumerDiscovererAPI<SruConsumerResource>>(){}));
		}
		
		if (builder.skipInitialize == true && builder.scope != null && this.endpoint != null){
			logger.info("requested to skip the initialize part");
		} else {
			this.initialize(discoverer, builder.scope);
		}
	}

	private void initialize(final SruConsumerDiscovererAPI<SruConsumerResource> discoverer,
			final String scope) throws SruConsumerClientException  {
		
		try {
			Set<String> SruConsumerNodes = discoverer.discoverSruConsumerNodeRunningInstances(scope);
	
			List<String> endpoints = Lists.newArrayList(SruConsumerNodes);
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					endpoints = Lists.newArrayList(this.endpoint);
				} else {
					throw new SruConsumerClientException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
				}
			} else {
				Collections.shuffle(endpoints);
			}
			
			this.endpoint = endpoints.get(0);
			
			logger.info("Initialized at : " + this.endpoint);
		} catch (Exception e) {
			logger.error("could not initialize random client", e);
			throw new SruConsumerClientException("could not initialize random client", e);
		}
		
	}
	
	
	public String createResource(SruConsumerResource resource, String scope) throws SruConsumerClientException{
		
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
			throw new SruConsumerClientException("resource could not be created : " + error);
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
	
	private static SruConsumerServiceFactoryAPI getSruServiceFactoryProxy(String endpoint) throws SruConsumerClientException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		SruConsumerServiceFactoryAPI simple = null;
		
		logger.info("getting proxy from index factory service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			simple = target.proxy(SruConsumerServiceFactoryAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint, e);
			throw new SruConsumerClientException("Client could not connect to endpoint : " + endpoint, e);
		}
		
		logger.info("getting proxy from index factory service...OK");
		
		return simple;
	}
	
	
	public static class ResourceBuilder {
		private String schema;
		private String host;
		private Integer port;
		private String servlet;
		private String version;
		private Long maxRecords;
		private String defaultRecordSchema;
		private List<String> presentables;
		private List<String> searchables;
		private String recordIDField;
		private String scope;
		private String collectionID;
		private Map<String, String> mapping;
		private String snippetField;
		
		public ResourceBuilder snippetField(String snippetField){
			this.snippetField = snippetField;
			return this;
		}
		
		public ResourceBuilder defaultRecordSchema(String defaultRecordSchema){
			this.defaultRecordSchema = defaultRecordSchema;
			return this;
		}
		
		public ResourceBuilder collectionID(String collectionID){
			this.collectionID = collectionID;
			return this;
		}
		
		public ResourceBuilder schema(String schema){
			this.schema = schema;
			return this;
		}
		
		public ResourceBuilder host(String host){
			this.host = host;
			return this;
		}
		
		public ResourceBuilder port(Integer port){
			this.port = port;
			return this;
		}
		
		public ResourceBuilder servlet(String servlet){
			this.servlet = servlet;
			return this;
		}
		
		public ResourceBuilder version(String version){
			this.version = version;
			return this;
		}
		
		public ResourceBuilder maxRecords(Long maxRecords){
			this.maxRecords = maxRecords;
			return this;
		}
		
		public ResourceBuilder presentables(List<String> presentables){
			this.presentables = presentables;
			return this;
		}
		
		public ResourceBuilder searchables(List<String> searchables){
			this.searchables = searchables;
			return this;
		}
		
		public ResourceBuilder recordIDField(String recordIDField){
			this.recordIDField = recordIDField;
			return this;
		}
		
		public ResourceBuilder scope(String scope){
			this.scope = scope;
			return this;
		}
		
		public ResourceBuilder mapping(Map<String, String> mapping){
			this.mapping = mapping;
			return this;
		}
		

		public SruConsumerResource build(){
			
			DescriptionDocument descriptionDocument = new DescriptionDocument();
			descriptionDocument.setSchema(this.schema);
			descriptionDocument.setHost(this.host);
			descriptionDocument.setPort(this.port);
			descriptionDocument.setServlet(this.servlet);
			descriptionDocument.setVersion(this.version);
			descriptionDocument.setMaxRecords(this.maxRecords);
			descriptionDocument.setDefaultRecordSchema(this.defaultRecordSchema);
			
			SruConsumerResource resource = new SruConsumerResource();
			
			resource.setDescriptionDocument(descriptionDocument);
			
			resource.setPresentables(this.presentables);
			resource.setSearchables(this.searchables);
			
			resource.setRecordIDField(this.recordIDField);
			
			resource.setCollectionID(this.collectionID);
			
			resource.setScope(this.scope);
			
			
			resource.setFields(createFields());
			resource.setCollections(Lists.newArrayList(this.collectionID));
			
			if (this.mapping == null || this.mapping.size() == 0){
				resource.setIsCustomMapped(false);
			} else {
				resource.setMapping(this.mapping);
			}
			
			return resource;
		}
		
		private List<String> createFields(){
			
			
			List<String> fields = Lists.newArrayList();
			
			final String defaultLang = "unknown";
			final String presentableType = "p";
			final String searchableType = "s";
			
			logger.info("will create fields for the collection : " + collectionID);
			
			if (this.presentables != null) {
				for (String field : this.presentables){
					String f = constructField(collectionID, field, defaultLang, presentableType);
					logger.info("adding presentable field : " + f);
					fields.add(f);
				}
			}
			
			if (this.snippetField != null) {
				String f = constructField(collectionID, "S", defaultLang, presentableType);
				logger.info("adding presentable field : " + f);
				fields.add(f);
			}
			
			if (this.searchables != null) {
				for (String field : this.searchables){
					String f = constructField(collectionID, field, defaultLang, searchableType);
					logger.info("adding searchable field : " + f);
					fields.add(f);
				}
			}
			
			String f = constructField(collectionID, "allIndexes", defaultLang, searchableType);
			logger.info("adding searchable field : " + f);
			fields.add(f);
				
			
			return fields;
		}
		
		static String constructField(String collectionID, String field, String language, String type){
			return collectionID + ":" + language + ":" + type + ":" + field;
		}
		
		public static SruConsumerResource createResourceFromExplain(String schema, String host, Integer port, String servlet, String collectionID) throws URISyntaxException, IOException {
			String urlString = constructExplainURLString(schema, host, port, servlet);
			
			String xml = IOUtils.toString(new URI(urlString), "UTF-8");
			
			XPathEvaluator eval =  new XPathEvaluator(XMLConverter.stringToNode(xml));
			
			List<String> indexes = null;
			
			indexes = eval.evaluate("/explainResponse/record/recordData/explain/indexInfo/index/title/text()");
			
			
//			String databaseTitle = null;
//			try {
//				databaseTitle = eval.evaluate("/explainResponse/record/recordData/explain/databaseInfo/title/text()").get(0);
//			} catch (Exception e) {
//				logger.warn("error while getting databaseTitle");
//			}
//			
//			String databaseDescription = null;
//			try {
//				databaseDescription = eval.evaluate("/explainResponse/record/recordData/explain/databaseInfo/description/text()").get(0);
//			} catch (Exception e) {
//				logger.warn("error while getting databaseDescription");
//			}
			
			List<String> schemas = eval.evaluate("/explainResponse/record/recordData/explain/schemaInfo/schema/['identifier']");
			
			String version = null;
			try {
				version = eval.evaluate("/explainResponse/version/text()").get(0);
			} catch (Exception e) {
				logger.warn("error while getting version");
			}
			
			Long maximumRecords = null; 
			try {
				maximumRecords = Long.valueOf(eval.evaluate("/explainResponse/record/recordData/explain/configInfo/default[@type='maximumRecords']/text()").get(0));
			} catch (Exception e) {
				logger.warn("error while getting maximumRecords");
			}
			
			String retrieveSchema = null;
			try {
				retrieveSchema = eval.evaluate("/explainResponse/record/recordData/explain/configInfo/default[@type='retrieveSchema']/text()").get(0);
			} catch (Exception e) {
				logger.warn("error while getting retrieveSchema");
			}
			
			
			logger.info("maximumRecords : " + maximumRecords);
			
			logger.info("schemas : " + schemas);
			
			logger.info("maximumRecords : " + maximumRecords);
			
			logger.info("retrieveSchema : " + retrieveSchema);
			
			logger.info("indexes : " + indexes);
			
			SruConsumerResource resource = new SruConsumerFactoryClient.ResourceBuilder()
				.schema(schema)
				.host(host)
				.port(port)
				.servlet(servlet)
				.collectionID(collectionID)
				.maxRecords(maximumRecords)
				.version(version)
				.defaultRecordSchema(retrieveSchema)
				.searchables(indexes)
				.presentables(indexes)
				.recordIDField("id")
				.build();
			
			
			return resource;
			
		}
		
		static String constructExplainURLString(String schema, String host, Integer port, String servlet) throws URISyntaxException {
			StringBuffer strBuf = new StringBuffer();
			strBuf.append("operation=explain");

			logger.info("uri : " + strBuf);
			
			String url = strBuf.toString();
			
			URI uri = new URI(
					schema,
					null,
					host,
					port,
					"/" + servlet,
					//URLEncoder.encode(strBuf.toString()),
					url,
					null
					);
			return uri.toString();

		}
	}
	
}
