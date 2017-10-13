package org.gcube.data.transfer.service.transfers.engine.impl;

import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.TransferTicket.Status;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;

public class RequestHandler extends AbstractTicketHandler implements Runnable {


	TicketManager ticketManager;

	@Override
	public void run() {
		handle();
	}
	
	
	public RequestHandler(TicketManager ticketManager,TransferTicket ticket,PersistenceProvider persProv, PluginManager plugMan) {
		super(persProv, plugMan,ticket);
		this.ticketManager=ticketManager;
		ticketManager.insertUpdate(ticket);
	}

	@Override
	protected void onStep(String msg, double progress, Status status, long transferredBytes) {
		super.onStep(msg, progress, status,transferredBytes);
		ticketManager.insertUpdate(getTicket());
	}

}
