package org.gcube.application.perform.service;

import org.gcube.application.perform.service.engine.impl.ImporterImpl;
import org.gcube.application.perform.service.engine.impl.PerformanceManagerImpl;
import org.gcube.application.perform.service.engine.utils.ScopeUtils;
import org.gcube.smartgears.ApplicationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PerformServiceManager implements ApplicationManager{

	
	private static final Logger log= LoggerFactory.getLogger(PerformServiceManager.class);
	
	
	
	
	@Override
	public void onInit() {
		try {
		
			PerformanceManagerImpl.initDatabase();
			new ImporterImpl().init();
		}catch(Throwable t) {
			log.warn("UNABLE TO INIT SERVICE UNDER SCOPE "+ScopeUtils.getCurrentScope(), t);
		}
	}
	
	
	@Override
	public void onShutdown() {
		// TODO Auto-generated method stub
		
	}
	
}
