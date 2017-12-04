package org.gcube.data.transfer.service;

import javax.inject.Inject;

import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;
import org.gcube.smartgears.ApplicationManager;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DTServiceAppManager implements ApplicationManager {
	 
		@Inject 
		RequestManager requests;
		@Inject 
		TicketManager tickets;
		@Inject
		PluginManager plugins;
		
		
		ApplicationContext ctx = ContextProvider.get();
	 
		@Override
		public void onInit() {
		  log.info("DT Application init");
		}
	 
		@Override
		public void onShutdown() {
			log.info("DT Application shutdown");
			log.debug("Shutting down request manager ...");
			requests.shutdown();
			log.debug("Shutting down ticket manager ...");
			tickets.shutdown();
			log.debug("Shutting down plugin manager ...");
			log.info("Done");
		}
	}