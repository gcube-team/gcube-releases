package org.gcube.data.transfer.service.transfers;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gcube.data.transfer.model.ServiceConstants;
import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.service.transfers.engine.RequestManager;

import lombok.extern.slf4j.Slf4j;


@Path(ServiceConstants.REQUESTS_SERVLET_NAME)
@Slf4j
public class Requests {

	@Inject
	RequestManager requests;


	
	//********************* INJECT PARAMS
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public TransferTicket submitRequest(@NotNull TransferRequest theRequest){
		log.info("Received transfer request : "+theRequest);	
		
		return requests.put(theRequest);
	}
	
	
//	@PUT
//	@Path("/{method}/{destinationId}/{subPath: .*}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public TransferTicket submitRESTRequest(@PathParam("method") String methodString, 
//			@PathParam("destinationId") String destinationID, @PathParam("subPath") String subPath){
//			return handleRequest(formRequestFromREST(methodString, destinationID, subPath));
//	}

	

	
	

//	private TransferTicket handleRequest(TransferRequest toHandle){
//		
//		toHandle.setId(UUID.randomUUID().toString());
//		boolean inserted=requests.put(toHandle);
//
//		log.debug("Successfully inserted "+inserted);
//
//		if(!inserted) throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
//		else
//			try {
//				return tickets.get(toHandle.getId());
//			} catch (TicketNotFoundException e) {
//				throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
//			}
//	}

}
