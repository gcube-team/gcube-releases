package org.gcube.data.transfer.service.transfers.engine.impl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalRequestHandler extends AbstractTicketHandler{

		public LocalRequestHandler(PersistenceProvider persProv, PluginManager plugMan, TransferTicket ticket,String accountingId) {
			super(persProv, plugMan, ticket,accountingId);
		}
		
		@Override
		protected void onStep(String msg, double progress, org.gcube.data.transfer.model.TransferTicket.Status status,
				long transferredBytes) {
			super.onStep(msg, progress, status, transferredBytes);				
			log.trace("Stepping upload. Relative Ticket {}  ",getTicket());				
		}
		
		@Override
		protected void onError(String message) {
			log.error("Unable to manage upload request ticket {} MSG {} ",getTicket(),message);
			throw new WebApplicationException("Internal ERROR "+message,Status.INTERNAL_SERVER_ERROR); 
		}
}
