package gr.cite.bluebridge.analytics.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author mnikolopoulos
 *
 */
public class SingletonHttpClient {

	private final Client client = Client.create();
	
	private static SingletonHttpClient singletonHttpClient = new SingletonHttpClient();
		
	private SingletonHttpClient(){}
	
	public static SingletonHttpClient getSingletonHttpClient(){
		return singletonHttpClient;
	}
	
	public Client getClient(){
		return client;
	}
	
	/**
	 * 
	 * @param resourceRequest The ResourceRequest request that you obtained form the controller
	 * @param acceptHeader The accept header (e.g "application/json") 
	 * @return returns a map from the call to the backend0
	 */
	public Map<String, Object> fetchBackEndData(ResourceRequest resourceRequest, String acceptHeader)  {
		try {
			
			String methodName = resourceRequest.getMethod();
			String url = returnUrl(resourceRequest);
			String body = "";
			
			ClientResponse clientResponse = null;
			WebResource webResource = client.resource(url);
			
			if (methodName.equals("POST") || methodName.equals("PUT")){
				body = createBody(resourceRequest);
				clientResponse = webResource.accept(acceptHeader).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, body);
			}else if(methodName.equals("GET")){
				clientResponse = webResource.accept(acceptHeader).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			}
			
		
			if (clientResponse.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : " + clientResponse.getStatus());
			}
		
			String output = clientResponse.getEntity(String.class);
			
			Map<String,Object> map = new ObjectMapper().readValue(output, Map.class);
			
			return map;
			
		  } catch (Exception e) {
		
			  throw new RuntimeException("An error occured during the connection with the Service.");
		
		  }
		
	}
	
	/**
	 * 
	 * @param resourceRequest 
	 * @return return the appropriate url based on the resourceRequest
	 */
	private static String returnUrl(ResourceRequest resourceRequest){
		
		Portlet portlet = PortletLocalServiceUtil.getPortletById(((ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY)).getPortletDisplay().getId());
		String backEndUrl = portlet.getInitParams().get("back-end-url");
		
		String controllerName = resourceRequest.getResourceID();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(backEndUrl);
		sb.append(controllerName);
		sb.append("?");
		for (Map.Entry<String, String[]> entry : resourceRequest.getParameterMap().entrySet()){
			
			sb.append(entry.getKey().toString());
			sb.append("=");
			sb.append(entry.getValue()[0]);
			sb.append("&");
		}
		
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
		
	}
	
	private static String createBody(ResourceRequest resourceRequest) throws IOException{
		String body = "";
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader reader = resourceRequest.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
		}
		body = stringBuilder.toString();
		return body;
	}
	
}
