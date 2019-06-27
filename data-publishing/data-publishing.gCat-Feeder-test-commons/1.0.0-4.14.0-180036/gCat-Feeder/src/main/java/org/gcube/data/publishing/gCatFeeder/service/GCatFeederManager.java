package org.gcube.data.publishing.gCatFeeder.service;

import javax.inject.Inject;

import org.gcube.data.publishing.gCatFeeder.service.engine.ExecutionManager;
import org.gcube.smartgears.ApplicationManager;

public class GCatFeederManager implements ApplicationManager{
	@Inject
	ExecutionManager executions;
	
	
	
	@Override
	public void onInit() {
			
		
	}
	
	
	@Override
	public void onShutdown() {
		executions.stop();
	}
	
	
}
