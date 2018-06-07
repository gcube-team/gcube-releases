package org.gcube.application.aquamaps.aquamapsspeciesview.servlet.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeciesFetchThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(SpeciesFetchThread.class);

	public SpeciesFetchThread() {
		this.setName("SpeciesFetchThread");
	}


	@Override
	public void run() {
		while(true)
			try{
				for(String scope:DBManager.getInitializedScopes()){
					try{
						logger.debug("Checking db under scope "+scope);
						DBInterface db=DBManager.getInstance(scope.toString());
						if(!db.isUpToDate())db.fetchSpecies();				
					}catch(Exception e){
						logger.error("Unable to refresh db under scope "+scope);
					}
				}
				Thread.sleep(3*60*1000);
			}catch(InterruptedException e){}
	}


}
