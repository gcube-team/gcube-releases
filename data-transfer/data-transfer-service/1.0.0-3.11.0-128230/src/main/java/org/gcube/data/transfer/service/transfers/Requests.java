package org.gcube.data.transfer.service.transfers;

import java.util.UUID;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.transfer.model.ServiceConstants;
import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;
import org.gcube.data.transfer.service.transfers.engine.faults.TicketNotFoundException;
import org.hibernate.validator.constraints.NotEmpty;


@Path(ServiceConstants.REQUESTS_SERVLET_NAME)
@Slf4j
public class Requests {

	@Inject
	RequestManager requests;
	
	@Inject
	TicketManager tickets;
	
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public TransferTicket submitRequest(@NotNull TransferRequest theRequest){
		log.debug("Received transfer request : "+theRequest);	
		theRequest.setId(UUID.randomUUID().toString());
		boolean inserted=requests.put(theRequest);
		
		log.debug("Successfully inserted "+inserted);
		
		if(!inserted) throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		else
			try {
				return tickets.get(theRequest.getId());
			} catch (TicketNotFoundException e) {
				throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
			}
	}
	
}
