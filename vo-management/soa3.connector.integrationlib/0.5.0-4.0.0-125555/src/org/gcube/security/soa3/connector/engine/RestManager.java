package org.gcube.security.soa3.connector.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * 
 * Singleton class which manages the REST calls for SOA3
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class RestManager 
{
	
	private Logger logger;
	private String soa3Endpoint;
	private Client client;
	private static Map<String, RestManager> instances = new HashMap<String, RestManager> ();
	
	public static RestManager getInstance (String soa3Endpoint)
	{
		RestManager currentInstance = instances.get(soa3Endpoint);
		
		if (currentInstance == null) 
		{
			currentInstance = new RestManager(soa3Endpoint);
			instances.put(soa3Endpoint, currentInstance);
		}
		
		return currentInstance;
	}
	
	
	private RestManager(String soa3Endpoint) 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.soa3Endpoint = soa3Endpoint;
		this.client = generateClient();
		
	}
	
	
	private Client generateClient ()
	{
		logger.debug("creating client...");
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		logger.debug("Client created");
		return client;
	}
	
	private WebResource generateWebResource (Client client, String completeUrl)
	{
		
		logger.debug("creating a web resource...");
		WebResource resource = client.resource(completeUrl);
		logger.debug("Web resource created");
		return resource;
	}

	private Builder getRequest (WebResource resource, MediaType acceptMediaType, MediaType mediaType)
	{
		logger.debug("setting JSon accept header...");
		Builder request = resource.accept(acceptMediaType).type(mediaType);
		logger.debug("Accept header set");
		return request;
	}
	
	/**
	 * 
	 * Sends the message
	 * 
	 * @param path the path of the call
	 * @param headers the headers map
	 * @param body the body of the message
	 * @param mediaType the media type
	 * @param type the response type 
	 * @return the response
	 */
	public synchronized String sendMessage (String path,Map<String, String> headers, String body, MediaType mediaType, MediaType acceptMediaType)
	{
	
		logger.debug("Authentication service");
		String completeUrl = this.soa3Endpoint+"/"+path;
		WebResource authenticationResource = generateWebResource(this.client, completeUrl);
		Builder authenticationBuilder = getRequest(authenticationResource,acceptMediaType,mediaType);
		logger.debug("Authentication web resource OK");

		Iterator<String> keys = headers.keySet().iterator();
		
		while (keys.hasNext())
		{
			String headerName = keys.next();
			String headerValue = headers.get(headerName);
			logger.debug("Adding header "+headerName+" value "+headerValue);
			authenticationBuilder = authenticationBuilder.header(headerName, headerValue);
			logger.debug("Header added");
		}

		logger.debug("Authentication builder OK");
		ClientResponse response = authenticationBuilder.get(ClientResponse.class);
		logger.debug("Response "+response);
		int status = response.getStatus();
		logger.debug("Status = "+status);
		String stringResponse = null;
		
		if (status>=200 && status<300)
		{
			stringResponse = response.getEntity(String.class);
			logger.debug("Response = "+stringResponse);
		}
		else logger.debug("Invalid response");
		return stringResponse;

	}
	

	public static void main(String[] args) {
		RestManager manager = new RestManager("http://192.168.125.249:8080/soa3Service" );
		Map<String, String> header = new HashMap<String, String>();
		header.put("Authorization", "BASIC dGVzdDpwYXNzd29yZA==");
		manager.sendMessage("access", header, null, MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_JSON_TYPE);
	}

}
