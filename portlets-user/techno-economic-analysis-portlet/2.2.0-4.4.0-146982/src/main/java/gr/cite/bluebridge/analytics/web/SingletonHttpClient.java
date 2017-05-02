package gr.cite.bluebridge.analytics.web;

import java.net.SocketTimeoutException;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SingletonHttpClient{
	
	private static ObjectMapper mapper = new ObjectMapper();
	private static Client singletonHttpClient;
	private static Integer HTTP_CONNECTION_TIMEOUT;	
	
	public static Client getSingletonHttpClient(){
		return singletonHttpClient == null ? singletonHttpClient = ClientBuilder.newClient() : singletonHttpClient;
	}	
	
	public Response doGet(String serviceUrl, Map<String, Object> headers){
		Client client = SingletonHttpClient.getSingletonHttpClient();
		client.property(ClientProperties.CONNECT_TIMEOUT, HTTP_CONNECTION_TIMEOUT);
		client.register(JacksonFeature.class);
		
		WebTarget webTarget = client.target(serviceUrl);
		Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		
		builder.accept(MediaType.APPLICATION_JSON);			
		for(Map.Entry<String, Object> entry : headers.entrySet()){
			builder.header(entry.getKey(), entry.getValue());
		}		

		Response response = builder.get();
		
		return response;
	}
	
	public Response doPost(String serviceUrl, Map<String, Object> headers, Object bodyObject){		
		Client client = SingletonHttpClient.getSingletonHttpClient();
		client.property(ClientProperties.CONNECT_TIMEOUT, HTTP_CONNECTION_TIMEOUT);
		client.register(JacksonFeature.class);
		
		WebTarget webTarget = client.target(serviceUrl);
		Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		
		builder.accept(MediaType.APPLICATION_JSON);			
		for(Map.Entry<String, Object> entry : headers.entrySet()){
			builder.header(entry.getKey(), entry.getValue());
		}	
		
		Invocation invocation = builder.buildPost(Entity.entity(bodyObject, MediaType.APPLICATION_JSON_TYPE));
		Response response = invocation.invoke();
		
		return response;
	}
	
	public Integer exceptionHandler(Exception e){
		if(e instanceof BadRequestException){						
			return 400;												
		}else if(e instanceof NotAuthorizedException){				
			return 401;											
		}else if(e instanceof ForbiddenException){					
			return 403;											
		}else if(e instanceof NotFoundException){	
			return 404;									
		}else if(e instanceof NotAllowedException){
			return 405; 									
		}else if(e instanceof NotAcceptableException){
			return 406;									
		}else if(e instanceof NotSupportedException){
			return 415; 									
		}else if(e instanceof InternalServerErrorException){
			return 500;										
		}else if(e instanceof ServiceUnavailableException){
			return 503;										
		}else if(e instanceof SocketTimeoutException){		
			return 504;										
		}else{														
			return 500;										
		}	
	}
	
	public static String toJson(Object bodyObject){
		String json = null;
		
		try {
			json = mapper.writeValueAsString(bodyObject);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	public Integer getHTTP_CONNECTION_TIMEOUT() {
		return HTTP_CONNECTION_TIMEOUT;
	}

	public void setHTTP_CONNECTION_TIMEOUT(Integer httpConnectionTimeout) {
		HTTP_CONNECTION_TIMEOUT = httpConnectionTimeout;
	}
}