package org.gcube.data.transfer.service.transfers.engine.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;

@Singleton
@Slf4j
public class RequestManagerImpl implements RequestManager{

	ExecutorService executor;
	
	
	private TicketManager manager;
	
	
	@Inject
	public RequestManagerImpl(TicketManager manager) {
		//TODO Implement ThreadFactory
		this.manager=manager;
		
		executor=Executors.newCachedThreadPool();		
	}
	
	
	
	@Override
	public boolean put(TransferRequest request) {
		try{
			executor.execute(new RequestHandler(request,manager));
			return true;
		}catch(Exception e){
			log.debug("Unexpected Error while creating handler",e);
			return false;
		}
	}

	
}
