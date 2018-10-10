package org.gcube.data.access.connector;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Iterator;
import java.util.List;

import org.gcube.data.access.connector.rest.entity.AccessibleCredentialsEntity;
import org.gcube.data.access.connector.rest.entity.SDIEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class TestGeonetworkConfiguration {
	
	private static String CONTEXT_MANAGER = "CONTEXT_MANAGER";
	private static String CONTEXT_USER = "CONTEXT_USER";

	public static void main(String[] args) {
		try {

			String url = "http://sdi-d-d4s.d4science.org/sdi-service/gcube/service/SDI?gcube-token=feda0617-cd9d-4841-b6f0-e047da5d32ed-98187548";
			RestTemplate restTemplate = new RestTemplate(); //getRestTemplate();

			//String response = restTemplate.getForObject(url, String.class);
			//System.out.println(response);
			
			ResponseEntity<SDIEntity> response = restTemplate.exchange(url, HttpMethod.GET, null, SDIEntity.class);
			System.out.println("Result " + response.getBody().getGeonetworkConfiguration().getBaseEndpoint() );
			//System.out.println("Result " + response.getBody().getGeoserverClusterConfiguration().getAvailableInstances().get(0).getBaseEndpoint() );
			
			System.out.println("Result " + getAccessibleCredentials().getAccessType());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public static AccessibleCredentialsEntity getAccessibleCredentials() {
		String url = "http://sdi-d-d4s.d4science.org/sdi-service/gcube/service/SDI?gcube-token=feda0617-cd9d-4841-b6f0-e047da5d32ed-98187548";
		RestTemplate restTemplate = new RestTemplate(); //getRestTemplate();

		String host = "geonetwork-sdi.dev.d4science.org";
		//String host = "geonetwork1-d-d4s.d4science.org";
		ResponseEntity<SDIEntity> response = restTemplate.exchange(url, HttpMethod.GET, null, SDIEntity.class);
		//System.out.println("Result " + response.getBody().getGeonetworkConfiguration().getBaseEndpoint() );
		//System.out.println("Result " + response.getBody().getGeoserverClusterConfiguration().getAvailableInstances().get(0).getBaseEndpoint() );
		
		String baseEndpoint = response.getBody().getGeonetworkConfiguration().getBaseEndpoint();
		AccessibleCredentialsEntity result = new AccessibleCredentialsEntity();

		if (baseEndpoint.contains(host)){		
			List<AccessibleCredentialsEntity> credentials = response.getBody().getGeonetworkConfiguration().getAccessibleCredentials();
			Iterator<AccessibleCredentialsEntity> iter = credentials.iterator();
			while (iter.hasNext()){
				AccessibleCredentialsEntity entity = iter.next();
				if (CONTEXT_MANAGER.equals(entity.getAccessType())){
					return entity;
				}	
				if ((result.getAccessType() == null) && CONTEXT_USER.equals(entity.getAccessType())){
					result = entity;
				}
			}
		}
		
		return result;
	}


	private static RestTemplate getRestTemplate() {

		final Authenticator authenticator = new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return (new PasswordAuthentication("username", "password".toCharArray()));
			}
		};

		Authenticator.setDefault(authenticator);

		final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		final InetSocketAddress address = new InetSocketAddress("proxy.eng.it", 3128);
		final Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
		factory.setProxy(proxy);

		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(factory);// pass the factory instance to restTemplate
		
		return restTemplate;
	}
}
