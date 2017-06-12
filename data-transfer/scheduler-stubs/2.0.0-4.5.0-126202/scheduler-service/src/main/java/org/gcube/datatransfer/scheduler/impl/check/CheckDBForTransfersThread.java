package org.gcube.datatransfer.scheduler.impl.check;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.Query;

import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.impl.newhandler.SchedulerUtils;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerResource;
import org.gcube.datatransfer.scheduler.is.ISManager;

public class CheckDBForTransfersThread extends Thread {

	public DataTransferDBManager dbManager=null;
	public SchedulerResource resource=null;
	public long checkForTransfersIntervalMS;
	public boolean immediateCheck;
	public boolean isMessagingEnabled;
	public long timeForSettingInactive;
	GCUBELog logger = new GCUBELog(CheckDBForTransfersThread.class);
	
	
	
	public CheckDBForTransfersThread(GCUBEWSResource ws) {
		this.dbManager=ServiceContext.getContext().getDbManager();	
		this.resource=(SchedulerResource) ws;
		this.checkForTransfersIntervalMS=1000 * Integer.valueOf((String) ServiceContext.getContext().getProperty("checkForTransfersIntervalInSeconds", true));
		this.immediateCheck=true; //the first time needs to check db
		this.isMessagingEnabled=ServiceContext.getContext().isMessagingEnabled();
		this.timeForSettingInactive=ServiceContext.getContext().getMaxTimeToSetInactiveAnOngoingTransferInMS();
	}

	public void run() {		
		int runs=0;
		long initialInterval=0;
		long tempInterval=0;
		logger.debug("\nCheckDBForTransfersThread - "+this.resource.getName()+" -- Thread has started");

		do {
			runs++;
			if(runs==1){
				this.sleepFiveSec();
			}
			
			// sleeping for checkForTransfersIntervalMS
			initialInterval=this.checkForTransfersIntervalMS;
			tempInterval=this.checkForTransfersIntervalMS;
			do {				
				this.sleepFiveSec();
				tempInterval=tempInterval-5000;
			}while(tempInterval>0 && initialInterval==this.checkForTransfersIntervalMS && this.immediateCheck==false);

			
			if(this.immediateCheck==true){
				this.sleepFiveSec();
				logger.debug("\nCheckDBForTransfersThread -- immediate check");
				this.immediateCheck=false;
			}
			else if (initialInterval!=this.checkForTransfersIntervalMS){
				logger.debug("\nCheckDBForTransfersThread -- interval has been changed from "+initialInterval+" MS to "+this.checkForTransfersIntervalMS+" MS");
			}
			
			//check long time ongoing transfers 
			checkLongTimeOngoing();
			
			//*** check for transfers ***
			//Retrieve all the activated transfers by query			
			Query query=null;
			List<Transfer> list=null;
			query = ServiceContext.getContext().getDbManager().getPersistenceManager().newQuery(Transfer.class);
			try{
				query.setFilter("status == \"STANDBY\" && submitter == \""+this.resource.getName()+"\"");
			}catch(Exception e){
				logger.error("\nCheckDBForTransfersThread -- Exception in retrieving all the activated transfers by query");
				e.printStackTrace();
			}			
			list = (List<Transfer>) query.execute();
					
			CheckDBForTransfers checkDBForTransfers = new CheckDBForTransfers(this.resource,list);
			checkDBForTransfers.check();
			
		} while (! Thread.interrupted());
	}

	public void sleepFiveSec(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			logger.error("\nCheckDBForTransfersThread (sleepFiveSec)-- InterruptedException-Unable to sleep");
			e.printStackTrace();
		}
	}
	
	public long getCheckForTransfersIntervalMS() {
		return checkForTransfersIntervalMS;
	}
	public void setCheckForTransfersIntervalMS(long checkForTransfersIntervalMS) {
		this.checkForTransfersIntervalMS = checkForTransfersIntervalMS;
	}
	public boolean isImmediateCheck() {
		return immediateCheck;
	}
	public void setImmediateCheck(boolean immediateCheck) {
		this.immediateCheck = immediateCheck;
	}
	
	public void resetCheckForTransfersInterval(){
		this.checkForTransfersIntervalMS=1000 * Integer.valueOf((String) ServiceContext.getContext().getProperty("checkForTransfersIntervalInSeconds", true));
	}
	
	public void checkLongTimeOngoing(){
		Query query=null;
		List<Transfer> list=null;
		query = ServiceContext.getContext().getDbManager().getPersistenceManager().newQuery(Transfer.class);
		try{
			query.setFilter("status == \"ONGOING\" && submitter == \""+this.resource.getName()+"\"");
		}catch(Exception e){
			logger.error("\nCheckDBForTransfersThread -- Exception in retrieving the ONGOING transfers by query");
			e.printStackTrace();
		}			
		list = (List<Transfer>) query.execute();
		for(Transfer transf:list){
		//retrieve startTime ... find how long it has been ongoing
		//switch it to failed if that time is more than e.g. 30minutes
			long currentTime = System.currentTimeMillis();
			long startTime = 0;			
			try{
				startTime=transf.getStartTime();
				if(startTime==-1)continue; // start time has not been set
			}
			catch(Exception e){e.printStackTrace();startTime=0;}
		
			long totalTime=currentTime-startTime;
			if(totalTime>timeForSettingInactive){
				String msg="CheckDBForTransfersThread(checkLongTimeOngoing) -- " +
						"Transfer with id="+transf.getTransferId()+" has been expired. It is ongoing more than 30 minutes..";
								
				String agentId=transf.getAgentId();
				Agent agent = null;
				boolean flagExists=false;
				if(agentId!=null){
					//retrieving the Agent by checking first if it exists 
					Extent<?> resultExtent = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
					Iterator<?> iter = resultExtent.iterator();
					while (iter.hasNext()){
						Agent obj=(Agent)iter.next();
						if(obj.getAgentId().compareTo(agentId)==0){
							agent=obj;
							flagExists=true;
							break;
						}
					}
				}
				if(flagExists==false){
					//then we should check for the agent in DB by host name because maybe it has been 
					//deleted/or deleted and then stored again(updated)
					
					ISManager isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();
					String checkResultFromDB=null;
					String hostnameOfAgent=transf.getAgentHostname();
					checkResultFromDB=isManagerForAgents.checkIfObjExistsInDB_ByHostname(hostnameOfAgent);
					if(checkResultFromDB!=null){
						try {
							this.dbManager.updateAgentInTransfer(transf.getTransferId(),checkResultFromDB);
						} catch (Exception e) {
							e.printStackTrace();
						}
						agent=null;
						//retrieving the Agent again...
						Extent<?> resultExtent2 = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
						Iterator<?> iter2 = resultExtent2.iterator();
						flagExists=false;
						while (iter2.hasNext()){
							Agent obj=(Agent)iter2.next();
							if(obj.getAgentId().compareTo(agentId)==0){
								agent=obj;
								flagExists=true;
								break;
							}
						}
						
						if(flagExists==false){
							msg=msg+"\nbut we cannot turn it failed because its agent does not exist " +
									"in the DB anymore and as a result we cannot update the statistics of that agent";
							logger.debug(msg);
							return;
						}
					}
					else{
						msg=msg+"\nbut we cannot turn it failed because its agent does not exist " +
								"in the DB anymore and as a result we cannot update the statistics of that agent";
						logger.debug(msg);
						return;
					}
				}			
				
				logger.debug(msg);
				List<String> errorsInTransfer=new ArrayList<String>();
				errorsInTransfer.add(msg);
				SchedulerUtils.updateStatusAndErrors(transf.getTransferId(),agent, "FAILED", errorsInTransfer);
			}
		}

	}
	
}