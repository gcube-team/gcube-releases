package gr.cite.geoanalytics.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;

public abstract class GeoanalyticsManagement {

	private Client jerseyClient;
	private ObjectMapper mapper;

	protected String authenticationStr;
	protected String HEADER_AUTHENTICATION_PARAM_NAME = "gcube-token";
	
	public GeoanalyticsManagement(){}
	
	public GeoanalyticsManagement(String authenticationStr){
		this.jerseyClient = Client.create();
		this.mapper = new ObjectMapper();
		this.authenticationStr = authenticationStr;
	}

	protected Client getJerseyClient() {
		return jerseyClient;
	}

	protected ObjectMapper getMapper() {
		return mapper;
	}
	
	
	
}
