package org.gcube.resources.federation.fhnmanager.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.federation.fhnmanager.is.ISProxyImpl;
import org.gcube.resources.federation.fhnmanager.occopus.OccopusNodeDefinitionImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ggiammat on 7/4/16.
 */
public class ISSynchronizer implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ISSynchronizer.class);

	private volatile boolean running;
	private long period;
	private List<String> scopes;

	

	public ISSynchronizer(List<String> scopes, long period) {
		this.running = true;
		this.period = period;
		this.scopes = scopes;
	}

	public void terminate() {
		running = false;
	}

	
	@Override
	public void run() {
		while (running) {
			try {
				LOGGER.debug("Sleeping for " + this.period / 1000 + " seconds");
				Thread.sleep(this.period);

				LOGGER.debug("Processing");
			
				ISProxyImpl a = new ISProxyImpl();
				FHNManagerImpl b = new FHNManagerImpl();
				//OccopusNodeDefinitionImporter c = new OccopusNodeDefinitionImporter();
				for(String scope : this.scopes){
					ScopeProvider.instance.set(scope);
					System.out.println("Updating IS on scope " + scope);
					a.updateIs();
					//System.out.println("Updating OccopusInfras on scope " + scope);
					//b.updateOccopusInfra();
				}
			} catch (Exception e) {
				LOGGER.error("Exception", e);
			}
		}
	}
}
 