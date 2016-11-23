package org.gcube.data.transfer.service;

import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.gcube.data.transfer.service.transfers.engine.impl.PersistenceProviderImpl;
import org.gcube.data.transfer.service.transfers.engine.impl.RequestManagerImpl;
import org.gcube.data.transfer.service.transfers.engine.impl.TransferTicketManagerImpl;
import org.glassfish.hk2.api.Factory;

public class RequestManagerFactory implements Factory<RequestManager> {
	
	@Override
	public void dispose(RequestManager instance) {
		
	}
	
	@Override
	public RequestManager provide() {
		return new RequestManagerImpl(new TransferTicketManagerImpl(),new PersistenceProviderImpl());
	}

}
