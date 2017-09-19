package org.gcube.data.transfer.library.client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.data.transfer.library.faults.CommunicationException;
import org.gcube.data.transfer.library.faults.RemoteServiceException;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.model.ServiceConstants;
import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {

	private static ClientConfig config=null;

	static{
		log.debug("Creating configuration ..");
		config=new ClientConfig();
		config.register(JacksonFeature.class);
		config.register(AuthorizationFilter.class);
	}

	private String endpoint;

	private WebTarget rootTarget;

	public Client(String endpoint) throws ServiceNotFoundException{
		try{
			log.debug("Creating client for base "+endpoint);
			this.endpoint=endpoint+"";
			rootTarget= ClientBuilder.newClient(config).target(endpoint).path("data-transfer-service").path(ServiceConstants.APPLICATION_PATH);
//			checkResponse(rootTarget.request().get());

			log.debug("Root Taget IS {} ",rootTarget.getUri());
		}catch(Exception e){
			throw new ServiceNotFoundException(e);
		}
	}


	public String getEndpoint() {
		return endpoint;
	}

	public TransferCapabilities getCapabilties() throws CommunicationException{
		WebTarget capabilitiesTarget=rootTarget.path(ServiceConstants.CAPABILTIES_SERVLET_NAME);
		log.debug("Getting capabilities from {}, path is {} ",endpoint,capabilitiesTarget.getUri());
		try{
			Response resp=capabilitiesTarget.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
			checkResponse(resp);
			return resp.readEntity(TransferCapabilities.class);
		}catch(Exception e){
			throw new CommunicationException(e);		
		}
	}


	public TransferTicket submit(TransferRequest request) throws RemoteServiceException{
		log.debug("Sending request {} to {}",request,endpoint);
		try{
			Response resp=rootTarget.path(ServiceConstants.REQUESTS_SERVLET_NAME).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(request,MediaType.APPLICATION_JSON_TYPE));
			checkResponse(resp);
			return resp.readEntity(TransferTicket.class);
		}catch(Exception e){
			throw new RemoteServiceException(e);
		}
	}

	public TransferTicket getTransferStatus(String transferId) throws RemoteServiceException{
		log.debug("Requesting transfer status [id = {}, endpoint={}]",transferId,endpoint);
		try{
			Response resp=rootTarget.path(ServiceConstants.STATUS_SERVLET_NAME).path(transferId).request(MediaType.APPLICATION_JSON_TYPE).get();
			checkResponse(resp);
			return resp.readEntity(TransferTicket.class);
		}catch(Exception e){
			throw new RemoteServiceException(e);
		}
	}


	protected void checkResponse(Response toCheck) throws Exception{
		switch(toCheck.getStatusInfo().getFamily()){		
		case SUCCESSFUL : break;
		default : throw new Exception("Unexpected Response code : "+toCheck.getStatus());
		}
	}
}
