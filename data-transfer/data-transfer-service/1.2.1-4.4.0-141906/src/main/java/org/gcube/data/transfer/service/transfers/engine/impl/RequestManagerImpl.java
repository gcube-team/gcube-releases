package org.gcube.data.transfer.service.transfers.engine.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;

import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class RequestManagerImpl implements RequestManager{

	ExecutorService executor;
	
	
	private TicketManager ticketManager;
	private PersistenceProvider persistenceProvider;
	private PluginManager pluginManager;
	
	@Inject
	public RequestManagerImpl(TicketManager ticketManager,PersistenceProvider persistenceProvider,PluginManager pluginManager) {
		//TODO Implement ThreadFactory
		this.ticketManager=ticketManager;
		this.persistenceProvider=persistenceProvider;
		executor=Executors.newCachedThreadPool();	
		this.pluginManager=pluginManager;
	}
	
	
	
	@Override
	public boolean put(TransferRequest request) {
		try{
			executor.execute(new RequestHandler(request,ticketManager,persistenceProvider,pluginManager));
			return true;
		}catch(Exception e){
			log.debug("Unexpected Error while creating handler",e);
			return false;
		}
	}

	
}
