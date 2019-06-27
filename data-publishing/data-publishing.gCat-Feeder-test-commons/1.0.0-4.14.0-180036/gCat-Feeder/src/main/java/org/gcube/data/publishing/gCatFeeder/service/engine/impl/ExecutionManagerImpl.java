package org.gcube.data.publishing.gCatFeeder.service.engine.impl;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.engine.CatalogueControllersManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.CollectorsManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.ExecutionManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.engine.LocalConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.engine.PersistenceManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.Storage;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExecutionManagerImpl implements ExecutionManager {

	private ThreadPoolExecutor executor=null; 
	private static final Logger log= LoggerFactory.getLogger(ExecutionManagerImpl.class);
	
	@Inject
	private PersistenceManager persistence;
	@Inject
	private CollectorsManager collectors;
	@Inject
	private CatalogueControllersManager catalogues;
	@Inject
	private Infrastructure infrastructure;
	@Inject
	private Storage storage;
	@Inject
	private LocalConfiguration config;
	@Inject
	private EnvironmentConfiguration environmentConfig;
	
	@PostConstruct
	private void post() {
		log.debug("Post Construct call : initializing pool..");
		executor=new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
	}
	
//	@Inject
//	public void setPersistence(PersistenceManager p) {
//		this.persistence=p;
//	}
//
//	@Inject 
//	public void setCollectorPluginManager(CollectorsManager c) {
//		this.collectors=c;
//	}
//
//	@Inject 
//	public void setCataloguesPluginManager(CatalogueControllersManager c) {
//		this.catalogues=c;
//	}
//
//	
//	@Inject
//	public void setInfastructureInterface(Infrastructure infra) {
//		this.infrastructure=infra;
//	}
//	@Inject 
//	public void setStorage(Storage storage) {
//		this.storage = storage;
//	}
	
	@Override
	public synchronized void submit(ExecutionDescriptor desc) {
		log.debug("Checking if {} is already in queue");
		ExecutionTask toSubmit=new ExecutionTask(desc);
		toSubmit.setCataloguesPluginManager(catalogues);
		toSubmit.setCollectorPluginManager(collectors);
		toSubmit.setInfastructureInterface(infrastructure);
		toSubmit.setPersistence(persistence);
		toSubmit.setStorage(storage);
		toSubmit.setEnvironmentConfiguration(environmentConfig);
		
		
		if(!executor.getQueue().contains(toSubmit)) {
			log.trace("Inserting execution in queue {} ");
			executor.execute(toSubmit);
			log.debug("Request submitted");
		}else {
			log.debug("Execution already in queue");
		}
		
	}

	@Override
	public void stop() {
		// TODO Clear queue
		log.debug("Stopping executors..");
		executor.shutdownNow();
		
	}

	@Override
	public void load() {
		// connect to persistence
		// load all pending
		throw new RuntimeException("NOT YET IMPLEMENTED");
	}

	

}
