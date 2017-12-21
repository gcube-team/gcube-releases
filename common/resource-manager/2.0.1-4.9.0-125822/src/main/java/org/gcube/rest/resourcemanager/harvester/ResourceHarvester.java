package org.gcube.rest.resourcemanager.harvester;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceRestAPI;
import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.resourcemanager.harvester.exceptions.ResourceHarvesterException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class ResourceHarvester<T extends StatefulResource> implements IResourceHarvester<T> {
	private static final Logger logger = LoggerFactory.getLogger(ResourceHarvester.class);
	private static final Integer CONNECTION_TIMEOUT_MILLIS = 30 * 1000;
	private static final Integer SOCKET_TIMEOUT_MILLIS = 30 * 1000;
	
	private static Gson gson = new Gson();

	@Inject
	public ResourceHarvester() {
		super();
	}

	@Override
	public T getResourceByID(String serviceEndpoint, String resourceID, Class<T> resourceCls, String scope) throws ResourceHarvesterException{
		
		ResourceAwareServiceRestAPI simple = null;
		
		try {
			simple = newClient(serviceEndpoint);
		} catch (ResourceHarvesterException e) {
			throw e;
		}
		
		Response response = simple.getResourceREST(scope, resourceID, false);
		String resource = response.readEntity(String.class);
		response.close();
		logger.info("resource json : " + resource);
		
		return gson.fromJson(resource, resourceCls);
	}
	
	@Override
	public Set<T> getResources(String serviceEndpoint, Class<T> resourceCls, String scope) throws ResourceHarvesterException {
		Set<String> resourceIDs = null;
		try {
			resourceIDs = getResourceIDs(serviceEndpoint, scope);
		} catch (ResourceHarvesterException e) {
			logger.error("error getting the resource ids from the endpoint : " + serviceEndpoint);
			throw e;
		}
		
		Set<T> resources = new HashSet<T>();
		logger.info("resource ids : " + resourceIDs);
		for (String resourceID : resourceIDs){
			logger.info("getting resource with id : " + resourceID);
			try {

				T resource = getResourceByID(serviceEndpoint, resourceID, resourceCls, scope);
				resources.add(resource);
			logger.info("resource got : " + resource.toJSON());
			} catch (Exception e) {
				logger.warn("error while trying to get resource with id : " + resourceID + " from endpoint : " + serviceEndpoint + " skipping this resource..", e);
			}
		}
		return resources;
	}
	
	
	public static Set<String> getResourceIDs(String serviceEndpoint, String scope) throws ResourceHarvesterException{
		logger.info("getResourceIDs... from endpoint : " + serviceEndpoint + " scope : " + scope);
		
		ResourceAwareServiceRestAPI simple = null;
		
		try {
			simple = newClient(serviceEndpoint);
		} catch (ResourceHarvesterException e) {
			throw e;
		}
		
		Response response = simple.listResourcesREST(scope, false, false);
		String resourcesJson = response.readEntity(String.class);
		response.close();
		logger.info("getResourceIDs...OK");
		logger.info("getResourceIDs json : " + resourcesJson);
		try {
			return gson.fromJson(resourcesJson, new TypeToken<Set<String>>(){}.getType());
		} catch (Exception e) {
			throw new ResourceHarvesterException("could not convert  : " + resourcesJson + " to set of strings", e);
		}
	}
	
	
	
	static ResourceAwareServiceRestAPI newClient(String endpoint) throws ResourceHarvesterException {
		ResteasyClient client = null;
		ResourceAwareServiceRestAPI simple = null;
		
		try {
			client = new ResteasyClientBuilder()
				.socketTimeout(SOCKET_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
				.establishConnectionTimeout(CONNECTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
				.build();
			
			simple = client
					.target(endpoint)
					.proxy(ResourceAwareServiceRestAPI.class);
			
			return simple; 
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint : " + endpoint,
					e);
			throw new ResourceHarvesterException("Client could not connect to endpoint : "
					+ endpoint, e);
		}
	}
	
}
