package org.gcube.datatransfer.scheduler.impl.check;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.DataSource;
import org.gcube.datatransfer.scheduler.db.model.DataStorage;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.is.ISManager;

public class CheckIS extends Thread {
	public DataTransferDBManager dbManager=null;
	public ISManager isManagerForAgents = null;
	public ISManager isManagerForSources = null;
	public ISManager isManagerForStorages = null;
	public static int checkISIntervalMS = 1000 * Integer.valueOf((String) ServiceContext.getContext().getProperty("checkISIntervalInSeconds", true));
	GCUBELog logger = new GCUBELog(CheckIS.class);

	public CheckIS(){
		this.dbManager=ServiceContext.getContext().getDbManager();
		this.isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();
		this.isManagerForSources=ServiceContext.getContext().getIsManagerForSources();
		this.isManagerForStorages=ServiceContext.getContext().getIsManagerForStorages();
	}


	public void run() {
		int runs=0;
		logger.debug("\nCheckIS -- Thread has started --");
		do {
			runs++;

			//we update the agents-data sources-storages in DB
			//more specifically we check if there's a new one in IS in order to store in DB
			// or if it's already exist in DB we change its status to UP .. 
			//Moreover we also check if a stored agent-source-storage doesn't have UP status in IS anymore so we change their status to DOWN.
			try{
				this.isManagerForAgents.updateObjsInDB();	
				this.isManagerForSources.updateObjsInDB();		
				this.isManagerForStorages.updateObjsInDB();	
			}catch(Exception e){
				logger.error("nCheckIS -- Exception in updating the agents-sources-storages");
				e.printStackTrace();
			}
			
			//for the first minute that container starts we check the IS more ofter
			if(runs<=6){
				this.sleep10Sec();
			}
			else{
				try {
					Thread.sleep(checkISIntervalMS);
				} catch (InterruptedException e) {
					logger.error("nCheckIS -- InterruptedException - Unable to sleep - circle of thread="+runs);
					e.printStackTrace();
				}
			}

		}while (! Thread.interrupted());
	}
	
	public void sleep10Sec(){
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			logger.error("\nCheckIS (sleep10Sec)-- InterruptedException-Unable to sleep");
			e.printStackTrace();
		}
	}
}
