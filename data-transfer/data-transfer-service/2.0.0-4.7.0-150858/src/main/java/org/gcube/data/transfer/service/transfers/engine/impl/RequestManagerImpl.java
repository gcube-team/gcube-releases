package org.gcube.data.transfer.service.transfers.engine.impl;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.options.TransferOptions.TransferMethod;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;

import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class RequestManagerImpl implements RequestManager{

	ExecutorService executor;

	TicketManager ticketManager;
	PersistenceProvider persistenceProvider;
	PluginManager pluginManager;



	@Inject
	public RequestManagerImpl(TicketManager ticketManager,PersistenceProvider persistenceProvider,PluginManager pluginManager) {		
		executor=Executors.newCachedThreadPool();	
		this.persistenceProvider=persistenceProvider;
		this.pluginManager=pluginManager;
		this.ticketManager=ticketManager;
	}



	@Override
	public TransferTicket put(TransferRequest request) {
		request.setId(UUID.randomUUID().toString());
		log.info("Managing request {} ",request);
		TransferTicket toReturn=new TransferTicket(request);

		if(request.getSettings().getOptions().getMethod().equals(TransferMethod.FileUpload)){
			log.debug("Request is sync");
			return new LocalRequestHandler(persistenceProvider, pluginManager, toReturn).handle();			
		}else{
			log.debug("Request is async");
			executor.execute(new RequestHandler(ticketManager,new TransferTicket(request),persistenceProvider,pluginManager));
			return toReturn;
		}
	}

@Override
public void shutdown() {
	log.debug("Calling shutdown..");
	executor.shutdownNow();
	
	long timeout=4;
	TimeUnit unit=TimeUnit.SECONDS;
	
	log.debug("Waiting termination.. {} {} ",timeout,unit);
	boolean halted=false;
	try {
		halted=executor.awaitTermination(timeout, unit);
	} catch (InterruptedException e) {
		log.debug("Halted threads : {} ",halted);
	}
}
}
