package org.gcube.data.transfer.service.transfers;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.gcube.data.transfer.model.ServiceConstants;
import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.data.transfer.service.transfers.engine.CapabilitiesProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path(ServiceConstants.CAPABILTIES_SERVLET_NAME)
public class Capabilities {

	@Inject
	CapabilitiesProvider provider;
	
	
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TransferCapabilities getCapabilities(){
		log.debug("Serving get capabilities");
		try{
			TransferCapabilities toReturn=provider.get();
			log.debug("No exceptions here.. returning "+toReturn);
			return toReturn;
		}catch(Exception e){
			log.debug("Unable to return capabilities.",e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
}
