package gr.cite.gos.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import javax.annotation.Resource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public abstract class GosManagement {
	
	private Client jerseyClient;
	private ObjectMapper mapper;

	protected String authenticationStr;
	protected String HEADER_AUTHENTICATION_PARAM_NAME = "gcube-token";
	
	public GosManagement(String authenticationStr){
		ClientConfig cc = new ClientConfig();

		cc.register(MultiPartFeature.class);
		this.jerseyClient = ClientBuilder.newClient(cc);

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
