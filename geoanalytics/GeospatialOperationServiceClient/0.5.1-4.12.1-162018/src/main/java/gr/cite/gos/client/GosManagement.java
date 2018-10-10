package gr.cite.gos.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.impl.MultiPartWriter;

public abstract class GosManagement {
	
	private Client jerseyClient;
	private ObjectMapper mapper;

	protected String authenticationStr;
	protected String HEADER_AUTHENTICATION_PARAM_NAME = "gcube-token";
	
	public GosManagement(String authenticationStr){
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(MultiPartWriter.class);
		
		this.jerseyClient = Client.create(cc);
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
