package org.gcube.soa3.connector.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import it.eng.rdlab.soa3.connector.beans.PolicyRequestBean;
import it.eng.rdlab.soa3.connector.beans.ServiceResponse;

import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

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
	
	private Log logger;
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
		this.logger = LogFactory.getLog(this.getClass());
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
	 * @param acceptMediaType the response media type
	 * @param type the response type 
	 * @return the response
	 */
	public synchronized <T extends ServiceResponse> T sendMessage (String path,Map<String, String> headers, String body, MediaType mediaType, MediaType acceptMediaType, Class <T> type)
	{
	
		logger.debug("REST GET service");
		String completeUrl = this.soa3Endpoint+"/"+path;
		WebResource authenticationResource = generateWebResource(this.client, completeUrl);
		Builder authenticationBuilder = getRequest(authenticationResource,acceptMediaType,mediaType);
		logger.debug("Web resource OK");
		
		
		Iterator<String> keys = headers.keySet().iterator();
		
		while (keys.hasNext())
		{
			String headerName = keys.next();
			String headerValue = headers.get(headerName);
			logger.debug("Adding header "+headerName+" value "+headerValue);
			authenticationBuilder = authenticationBuilder.header(headerName, headerValue);
			logger.debug("Header added");
		}

		logger.debug("Builder OK");
		ClientResponse response = authenticationBuilder.get(ClientResponse.class);
		logger.debug("Response "+response);
		int status = response.getStatus();
		logger.debug("Status = "+status);
		
		try
		{
			T serviceResponse = type.newInstance();
			serviceResponse.setResponseCode(status);
			
			if (status>=200 && status<300)
			{
				serviceResponse.importEntity(response);
				logger.debug("Response = "+serviceResponse);
			}
		
		return serviceResponse;
		
		}
		catch (Exception e)
		{
			logger.fatal("Unexpected exception",e);
			return null;
		}

	}
	

	/**
	 * 
	 * Sends the message
	 * 
	 * @param path the path of the call
	 * @param headers the headers map
	 * @param body the body of the message
	 * @param mediaType the media type
	 * @param acceptMediaType the response media type
	 * @param type the response type 
	 * @return the response
	 */
	public synchronized int sendPostMessage (String path,Map<String, String> headers, Object requestEntity, MediaType mediaType, MediaType acceptMediaType)
	{
	
		logger.debug("REST POST service");
		String completeUrl = this.soa3Endpoint+"/"+path;
		WebResource authenticationResource = generateWebResource(this.client, completeUrl);
		Builder authenticationBuilder = getRequest(authenticationResource,acceptMediaType,mediaType);
		logger.debug("Web resource OK");
		
		Iterator<String> keys = headers.keySet().iterator();
		
		while (keys.hasNext())
		{
			String headerName = keys.next();
			String headerValue = headers.get(headerName);
			logger.debug("Adding header "+headerName+" value "+headerValue);
			authenticationBuilder = authenticationBuilder.header(headerName, headerValue);
			logger.debug("Header added");
		}

		logger.debug("builder ok");
		ClientResponse response = authenticationBuilder.post(ClientResponse.class, serializeAsString(requestEntity));
		logger.debug("Response "+response);
		int status = response.getStatus();
		logger.debug("Status = "+status);
		return status;
	
	}
	

	private Object serializeAsString (Object input)
	{
		logger.debug("Serialization");
		Object response = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.getSerializationConfig().appendAnnotationIntrospector(new JaxbAnnotationIntrospector());
			response = mapper.writeValueAsString(input);
		} catch (Exception e)
		{
			logger.error("Unable to serialize the object,  trying to send the complete entity",e);
			response = input;
		}
		logger.debug("Entity = "+response);
		return response;
	}
	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.getSerializationConfig().appendAnnotationIntrospector(new JaxbAnnotationIntrospector());
		
		PolicyRequestBean bean = new PolicyRequestBean();
		bean.setAction("prova");
		bean.setResource("ciao");
		bean.getAttributes().add("ruolo");
		
		System.out.println(mapper.writeValueAsString(bean));
	}

}
