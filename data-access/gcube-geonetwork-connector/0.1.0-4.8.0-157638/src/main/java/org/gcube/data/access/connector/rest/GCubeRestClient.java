package org.gcube.data.access.connector.rest;

import java.util.Iterator;
import java.util.List;

import org.gcube.data.access.connector.rest.entity.AccessibleCredentialsEntity;
import org.gcube.data.access.connector.rest.entity.SDIEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import net.sf.json.JSONObject;

public class GCubeRestClient {
	
	private static String CONTEXT_MANAGER = "CONTEXT_MANAGER";
	private static String CONTEXT_USER = "CONTEXT_USER";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public AccessibleCredentialsEntity getAccessibleCredentials(String url) {

		logger.warn("REST call to URL: " + url);
		RestTemplate restTemplate = new RestTemplate();

		try {
			String response = restTemplate.getForObject(url, String.class);
			logger.warn("JSON response: \n" + response);

			JSONObject jsonObject = JSONObject.fromObject(response);
			return (AccessibleCredentialsEntity) JSONObject.toBean(jsonObject, AccessibleCredentialsEntity.class);
		} catch (Exception e) {
			logger.error("Error in getAccessibleCredentials() method: " + e.getMessage());
			return new AccessibleCredentialsEntity();
		}
	}
	
	public AccessibleCredentialsEntity getGeneralAccessibleCredentials(String url, String host) {
		
		logger.warn("REST call to URL: " + url);
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			ResponseEntity<SDIEntity> response = restTemplate.exchange(url, HttpMethod.GET, null, SDIEntity.class);
			
			String baseEndpoint = response.getBody().getGeonetworkConfiguration().getBaseEndpoint();
			AccessibleCredentialsEntity result = new AccessibleCredentialsEntity();

			if (baseEndpoint.contains(host)){	
				//get credentials from geonetworkConfiguration - to give priority on CONTEXT_MANAGER (if it exists), otherwise CONTEXT_USER
				List<AccessibleCredentialsEntity> credentials = response.getBody().getGeonetworkConfiguration().getAccessibleCredentials();
				Iterator<AccessibleCredentialsEntity> iter = credentials.iterator();		
				while (iter.hasNext()){
					AccessibleCredentialsEntity entity = iter.next();
					if (CONTEXT_MANAGER.equals(entity.getAccessType())){
						result = entity;
					}
					if ((result.getAccessType() == null) && CONTEXT_USER.equals(entity.getAccessType())){
						result = entity;
					}
				}	
			}
			
			return result;
		} catch (Exception e) {
			logger.error("Error in getGeneralAccessibleCredentials() method: " + e.getMessage());
			return new AccessibleCredentialsEntity();
		}
	}
}
