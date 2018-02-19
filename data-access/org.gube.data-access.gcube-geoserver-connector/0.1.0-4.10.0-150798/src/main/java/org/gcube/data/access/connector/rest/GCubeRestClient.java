package org.gcube.data.access.connector.rest;

import org.gcube.data.access.connector.rest.entity.AccessibleCredentialsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import net.sf.json.JSONObject;

public class GCubeRestClient {

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
	
}
