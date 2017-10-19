package org.gcube.application.aquamaps.aquamapsportlet.servlet;

import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBInterface;
import org.gcube.application.aquamaps.aquamapsportlet.servlet.db.DBManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FetchDataThread extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(FetchDataThread.class);


	public FetchDataThread() {		
		this.setName("FetchDataThread"+this.getId());
	}


	@Override
	public void run() {
		while(true){
			for(String scope:DBManager.getInitializedScopes()){
				try{
					logger.debug("Checking db in scope "+scope);
					DBInterface db=DBManager.getInstance(scope.toString());
					//						try{
					//							logger.debug("starting species information fetching for scope "+scope.getName());
					//							long before=System.currentTimeMillis();
					//							zipFile=config.getDMInterface(scope).exportTableAsCSV("speciesoccursum");
					//							csvFile=File.createTempFile("specs", ".csv");
					//							zipFile.renameTo(csvFile);
					//							int importCount=Utils.get().getDb(scope).importSpeciesOccursumCSV(csvFile);				
					//							logger.trace("Imported "+importCount+" species in "+(System.currentTimeMillis()-before)+" ms");
					//						}catch(Exception e){
					//							logger.error("Unable to fetch species summary from service under scope "+scope.getName(),e);
					//						}finally{
					//							try{if(csvFile!=null&&csvFile.exists())FileUtils.delete(csvFile);}
					//							catch(Exception e){logger.error("Unable to delete csv File "+csvFile.getAbsolutePath());}
					//							try{if(zipFile!=null&&zipFile.exists())FileUtils.delete(zipFile);}
					//							catch(Exception e){logger.error("Unable to delete csv File "+zipFile.getAbsolutePath());}
					//						}
					//					}

					if(!db.isUpToDate()) db.fetchSpecies();				
				}catch(Exception e){
					logger.error("Unexpected Exception",e);
				}			
			}
			int minutes=10;	
			logger.debug("Going to execute again in "+minutes+" minutes");
			try { Thread.sleep(minutes*60*1000);} catch (InterruptedException e) {}
		}

	}

}
