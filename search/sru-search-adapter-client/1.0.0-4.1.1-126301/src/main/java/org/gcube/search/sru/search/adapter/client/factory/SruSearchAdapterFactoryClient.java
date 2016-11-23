package org.gcube.search.sru.search.adapter.client.factory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.gcube.search.sru.search.adapter.client.exception.SruSearchAdapterClientException;
import org.gcube.search.sru.search.adapter.client.inject.SruSearchAdapterClientModule;
import org.gcube.search.sru.search.adapter.commons.apis.SruSearchAdapterServiceFactoryAPI;
import org.gcube.search.sru.search.adapter.commons.discoverer.SruSearchAdapterDiscovererAPI;
import org.gcube.search.sru.search.adapter.commons.resources.SruSearchAdapterResource;
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

public class SruSearchAdapterFactoryClient {
	private static Gson gson = new Gson();

	private String endpoint;
	
	private static final Logger logger = LoggerFactory.getLogger(SruSearchAdapterFactoryClient.class);
	
	public String getEndpoint() {
		return this.endpoint;
	}

	public static class Builder {
		private String endpoint;
		private String scope;

		private final SruSearchAdapterDiscovererAPI<SruSearchAdapterResource> discoverer;

		public Builder() {
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
		
		public Builder scope(String scope){
			this.scope = scope;
			return this;
		}
		
		public SruSearchAdapterFactoryClient build() throws SruSearchAdapterClientException {
			return new SruSearchAdapterFactoryClient(this);
		}
	}
	
	SruSearchAdapterFactoryClient(Builder builder) throws SruSearchAdapterClientException  {
		this.endpoint = builder.endpoint;
		
		SruSearchAdapterDiscovererAPI<SruSearchAdapterResource> discoverer = builder.discoverer;
		
		if (discoverer == null) {
			Injector injector = Guice.createInjector(new SruSearchAdapterClientModule());
			discoverer = injector.getInstance(Key.get(new TypeLiteral<SruSearchAdapterDiscovererAPI<SruSearchAdapterResource>>(){}));
		}
		
		this.initialize(discoverer, builder.scope);
	}

	private void initialize(final SruSearchAdapterDiscovererAPI<SruSearchAdapterResource> discoverer,
			final String scope) throws SruSearchAdapterClientException  {
		
		try {
			Set<String> srudbNodes = discoverer.discoverSruSearchAdapterNodeRunningInstances(scope);
	
			List<String> endpoints = Lists.newArrayList(srudbNodes);
			
			if (this.endpoint != null) {
				if (endpoints.contains(this.endpoint)){
					endpoints = Lists.newArrayList(this.endpoint);
				} else {
					throw new SruSearchAdapterClientException("could not initialize random client. given endpoint : " + this.endpoint + " found endpoints : " + endpoints);
				}
			} else {
				Collections.shuffle(endpoints);
			}
			
			this.endpoint = endpoints.get(0);
			
			logger.info("Initialized at : " + this.endpoint);
		} catch (Exception e) {
			logger.error("could not initialize random client", e);
			throw new SruSearchAdapterClientException("could not initialize random client", e);
		}
		
	}
	
	
	public String createResource(SruSearchAdapterResource resource, String scope) throws SruSearchAdapterClientException{
		
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
			throw new SruSearchAdapterClientException("resource could not be created : " + error);
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
	
	private static SruSearchAdapterServiceFactoryAPI getSruServiceFactoryProxy(String endpoint) throws SruSearchAdapterClientException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;
		SruSearchAdapterServiceFactoryAPI simple = null;
		
		logger.info("getting proxy from index factory service...");
		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(endpoint);
			simple = target.proxy(SruSearchAdapterServiceFactoryAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint, e);
			throw new SruSearchAdapterClientException("Client could not connect to endpoint : " + endpoint, e);
		}
		
		logger.info("getting proxy from index factory service...OK");
		
		return simple;
	}
	
}
