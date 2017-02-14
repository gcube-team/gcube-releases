package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSPECGroupMonitor extends Thread {


	final static Logger logger= LoggerFactory.getLogger(HSPECGroupMonitor.class);


	public HSPECGroupMonitor() throws Exception{
		super("HSPEC MONITOR");
	}


	@Override
	public void run() {
		while(true){
			try{
				for(SourceGenerationRequest request:TableGenerationExecutionManager.getAvailableRequests()){
					logger.trace("Found pending hspec request, ID : "+request.getId());
					TableGenerationExecutionManager.start(request);
				}
			}catch(Exception e){
				logger.error("Unexpected Exception", e);
			}
		}
	}
}
