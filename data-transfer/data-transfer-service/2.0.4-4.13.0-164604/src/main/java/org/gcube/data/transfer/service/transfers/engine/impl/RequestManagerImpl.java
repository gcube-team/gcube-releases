package org.gcube.data.transfer.service.transfers.engine.impl;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.options.TransferOptions.TransferMethod;
import org.gcube.data.transfer.service.transfers.engine.AccountingManager;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;
import org.gcube.smartgears.ContextProvider;

import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class RequestManagerImpl implements RequestManager{

	ExecutorService executor;

	TicketManager ticketManager;
	PersistenceProvider persistenceProvider;
	PluginManager pluginManager;
	AccountingManager accounting;


	@Inject
	public RequestManagerImpl(TicketManager ticketManager,PersistenceProvider persistenceProvider) {
		log.info("Checking pool configuration..");
		int coreSize=5;
		int maximumSize=10;
		long maximumIdleTime=60000;
		
		
		try {
		Properties props=new Properties();
		props.load(ContextProvider.get().application().getResourceAsStream("config.properties"));
		coreSize=Integer.parseInt(props.getProperty("transfers.poolCoreSize"));
		maximumSize=Integer.parseInt(props.getProperty("transfers.poolMaximumSize"));
		maximumIdleTime=Long.parseLong(props.getProperty("transfers.threadMaxIdleTimeMs"));
		}catch(Throwable t) {
			log.warn("****************************************************************************");
			log.warn("Unable to read configuration, reverting to default pool values ");
			log.warn("Core size {} , maximum size {} , maximum idle time {}",coreSize,maximumSize,maximumIdleTime);
			log.warn("Error was ",t);
		}
		
		executor=new ThreadPoolExecutor(coreSize, maximumSize, maximumIdleTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());			
		this.persistenceProvider=persistenceProvider;
		this.pluginManager=PluginManager.get();
		this.ticketManager=ticketManager;
		this.accounting=AccountingManager.get();
	}



	@Override
	public TransferTicket put(TransferRequest request) {
		request.setId(UUID.randomUUID().toString());
		log.info("Managing request {} ",request);
		TransferTicket toReturn=new TransferTicket(request);
		
		String accountingId=accounting.createNewRecord();
		
		
		
		
		if(request.getSettings().getOptions().getMethod().equals(TransferMethod.FileUpload)){
			log.debug("Request is sync");
			return new LocalRequestHandler(persistenceProvider, pluginManager, toReturn,accountingId).handle();			
		}else{
			log.debug("Request is async");
			executor.execute(new RequestHandler(ticketManager,new TransferTicket(request),persistenceProvider,pluginManager,accountingId));
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
