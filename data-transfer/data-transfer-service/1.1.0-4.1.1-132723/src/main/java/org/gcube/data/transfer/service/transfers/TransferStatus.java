package org.gcube.data.transfer.service.transfers;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.transfer.model.ServiceConstants;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;
import org.gcube.data.transfer.service.transfers.engine.faults.TicketNotFoundException;

@Path(ServiceConstants.STATUS_SERVLET_NAME)
@Slf4j
public class TransferStatus {

	@Inject
	private TicketManager manager;
	
	
	
		@GET
		@Path("/{"+ServiceConstants.TRANSFER_ID+"}")
		@Produces(MediaType.APPLICATION_XML)
		public TransferTicket getTicket(@PathParam(ServiceConstants.TRANSFER_ID)@NotNull String requestId){
			try{
				log.debug("Returning status for id "+requestId);
				return manager.get(requestId);
			}catch(TicketNotFoundException e){
				throw new NotFoundException();
			}catch(Throwable t){
				log.error("Unexpected exception ",t);
				throw new InternalServerErrorException();
			}
		}
		
		
}
