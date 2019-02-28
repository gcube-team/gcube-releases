package org.gcube.data.transfer.plugins.thredds;

import java.util.concurrent.Callable;

import org.gcube.data.transfer.plugin.model.DataTransferContext;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationConfigurationRetriever implements Callable<ApplicationConfiguration>{

	private DataTransferContext ctx;
	
	private long timeout=LocalConfiguration.getTTL(LocalConfiguration.CONTEXT_LOADING_TIMETOUT);
	
	public ApplicationConfigurationRetriever(DataTransferContext ctx) {
		this.ctx=ctx;		
	}
	

	@Override
	public ApplicationConfiguration call() throws Exception {
		ApplicationConfiguration toReturn=null;
		log.info("Waiting for thredds application to be loaded");
		long startTime=System.currentTimeMillis();
		while(toReturn==null&(System.currentTimeMillis()-startTime<timeout)) {
			try{Thread.sleep(1000);
			}catch(InterruptedException e) {}			
			for(ApplicationConfiguration app:ctx.getCtx().container().configuration().apps()) {
				log.debug("Found app {} ",app.context());
				if(app.context().equals("thredds")||app.context().equals("/thredds")) {
					toReturn=app;				
				}
			}
		}
		log.info("Retrieved {} after {}ms ",toReturn,(System.currentTimeMillis()-startTime));
		return toReturn;
	}


	
}
