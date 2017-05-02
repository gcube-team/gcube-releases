package gr.cite.gos.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;

public abstract class GosManagement {
	
	private Client jerseyClient;
	private ObjectMapper mapper;

	protected String authenticationStr;
	protected String HEADER_AUTHENTICATION_PARAM_NAME = "gcube-token";
	
	public GosManagement(String authenticationStr){
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
