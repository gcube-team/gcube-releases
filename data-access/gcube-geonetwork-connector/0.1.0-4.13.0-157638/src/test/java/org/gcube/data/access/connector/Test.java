package org.gcube.data.access.connector;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Test {

	public static void main(String[] args) {
		try {
			String url = "http://geonetwork-sdi.dev.d4science.org/geonetwork/srv/api/0.1/me";
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			
			String token = "admin:admin";
			headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(token.getBytes()));
			
	        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
	        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	        System.out.println("Result - status ("+ response.getStatusCode() + ") has body: " + response.hasBody());
	        System.out.println(response.getBody());
//			String response = restTemplate.getForObject(url, String.class);
//			System.out.println(response);
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}
