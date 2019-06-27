package org.gcube.data.publishing.gCatFeeder.service;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.publishing.gCatFeeder.service.engine.CatalogueControllersManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.CollectorsManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.ExecutionManager;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InternalError;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleHandler;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Start;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent.Stop;

@XmlRootElement(name = "gcatFeeder-lifecycle")
public class GCatFeederLyfeCycleManager extends ApplicationLifecycleHandler{

	@Inject
	ExecutionManager executions;
	
	@Inject
	CollectorsManager collectors;
	
	@Inject
	CatalogueControllersManager controllers;
	
	
	@Override
	public void onStart(Start e) {
		super.onStart(e);
		
		try {
			collectors.initInScope();
			controllers.initInScope();
		} catch (InternalError ex) {
			throw new RuntimeException("Initialization Error",ex); 
		}	
		
		executions.load();
	}
	
	
	@Override
	public void onStop(Stop e) {		
		super.onStop(e);
		
		
		
		
	}
	
}
