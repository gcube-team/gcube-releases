package org.gcube.data.transfer.service.transfers.engine;

import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;

public interface RequestManager {

	public TransferTicket put(TransferRequest request);
	
	public void shutdown();
	
}
