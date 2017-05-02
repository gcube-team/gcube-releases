package org.gcube.data.transfer.service.transfers.engine;

import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.service.transfers.engine.faults.TicketNotFoundException;

public interface TicketManager {

	
	/**
	 * returns true if new
	 * 
	 * @param toInsert
	 * @return
	 */
	public boolean insertUpdate(TransferTicket toInsert);
	
	public TransferTicket get(String ticketId) throws TicketNotFoundException;
}
