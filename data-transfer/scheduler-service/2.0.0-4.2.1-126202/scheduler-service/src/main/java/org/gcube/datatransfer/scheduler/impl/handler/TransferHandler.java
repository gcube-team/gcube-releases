package org.gcube.datatransfer.scheduler.impl.handler;


import static org.gcube.datatransfer.agent.library.proxies.Proxies.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jdo.Extent;
import javax.jdo.Query;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.AnyHolder;
import org.gcube.datatransfer.common.agent.Types.DestData;
import org.gcube.datatransfer.common.agent.Types.MonitorTransferReportMessage;
import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageAccessType;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.outcome.TreeTransferOutcome;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.AgentStatistics;
import org.gcube.datatransfer.scheduler.db.model.DataSource;
import org.gcube.datatransfer.scheduler.db.model.DataStorage;
import org.gcube.datatransfer.scheduler.db.model.PeriodicallyScheduled;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.db.model.TransferObject;
import org.gcube.datatransfer.scheduler.db.model.TransferOutcome;
import org.gcube.datatransfer.scheduler.db.model.TypeOfSchedule;
import org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerResource;
import org.gcube.datatransfer.scheduler.impl.utils.Utils;
import org.gcube.datatransfer.scheduler.is.ISManager;
import org.gcube.datatransfer.scheduler.stubs.datatransferscheduler.SourceData;

import com.thoughtworks.xstream.XStream;



public class TransferHandler extends Thread {
	/** The UUIDGen */
	private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();
	Transfer transfer;
	String submitter;
	String transferId;
	DataTransferDBManager dbManager;
	SchedulerResource resource;
	String transferType;
	long checkForMonitorResultIntervalMS;
	long maxTimeForMonitoringWithQueuedResultMS;

	AgentLibrary agentLibrary = null;
	List<String> objectTrasferredIDs = new ArrayList<String>();
	List<String> objectFailedIDs = new ArrayList<String>();
	boolean failed=false;	
	List<String> errorsInTransfer = new ArrayList<String>();
	Agent agent;
	String hostAgent;
	int portAgent;

	GCUBELog logger = new GCUBELog(TransferHandler.class);
	ISManager isManagerForAgents;


	public TransferHandler(String transferId, GCUBEWSResource ws,boolean isPeriodically){
		this.dbManager=ServiceContext.getContext().getDbManager();
		this.transferId=transferId;		
		this.resource=(SchedulerResource) ws;
		this.transfer=this.dbManager.getPersistenceManager().getObjectById(Transfer.class, this.transferId);
		this.agent=null;
		this.submitter=this.transfer.getSubmitter();		
		this.transferType=this.transfer.getTransferType();
		this.checkForMonitorResultIntervalMS=1000 * Integer.valueOf((String) ServiceContext.getContext().getProperty("checkForMonitorResultIntervalInSeconds", true));
		this.maxTimeForMonitoringWithQueuedResultMS=1000 * Integer.valueOf((String) ServiceContext.getContext().getProperty("maxTimeForMonitoringWithQueuedResultInSeconds", true));
		this.isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();

	}

	public void run() {
		logger.debug("\nTransferHandler - "+this.resource.getName()+" -- Thread has started");

		String agentId = this.transfer.getAgentId();
		if(agentId==null){
			logger.error("TransferHandler - Error - agentId=null");
			errorsInTransfer.add("TransferHandler - Error - agentId=null");
			this.updateStatusAndErrors(this.transferId, "FAILED",errorsInTransfer);
			return;
		}
		//retrieving the Agent by checking first if it exists 
		Extent<?> resultExtent = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
		Iterator<?> iter = resultExtent.iterator();
		boolean flagExists=false;
		while (iter.hasNext()){
			Agent obj=(Agent)iter.next();
			if(obj.getAgentId().compareTo(agentId)==0){
				agent=obj;
				hostAgent=obj.getHost();
				portAgent=obj.getPort();
				flagExists=true;
				break;
			}
		}
		if(flagExists==false){
			//then we should check for the agent in DB by host name because maybe it has been 
			//deleted/or deleted and then stored again(updated)

			String checkResultFromDB=null;
			String hostnameOfAgent=transfer.getAgentHostname();
			checkResultFromDB=this.isManagerForAgents.checkIfObjExistsInDB_ByHostname(hostnameOfAgent);
			if(checkResultFromDB!=null){
				try {
					this.dbManager.updateAgentInTransfer(transferId,checkResultFromDB);
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
						hostAgent=obj.getHost();
						portAgent=obj.getPort();
						flagExists=true;
						break;
					}
				}

				if(flagExists==false){
					String errorMsg="TransferHandler -  Error - agent with hostname="+hostnameOfAgent+" does not exist in DB anymore";
					logger.error(errorMsg);
					errorsInTransfer.add(errorMsg);
					this.updateStatusAndErrors(this.transferId, "FAILED",errorsInTransfer);
					return;
				}
			}
			else{
				String errorMsg="TransferHandler -  Error - agent with hostname="+hostnameOfAgent+" does not exist in DB anymore";
				logger.error(errorMsg);
				errorsInTransfer.add(errorMsg);
				this.updateStatusAndErrors(this.transferId, "FAILED",errorsInTransfer);
				return;
			}
		}

		if(hostAgent==null){
			logger.error("TransferHandler - Error - hostAgent=null");
			errorsInTransfer.add("TransferHandler - Error - hostAgent=null");
			this.updateStatusAndErrors(this.transferId, "FAILED",errorsInTransfer);
			return;
		}

		//updating the agent statistics - increase the ongoing transfers
		updateAgentStatistics("ONGOING");
		//add the transfer id to the resource array of active transfers
		addActiveTransferResource();

		//    *******TRANSFER*******    //
		if(this.transferType.compareTo("LocalFileBasedTransfer")==0){
			logger.error("TransferHandler - Error - LocalFileBasedTransfer is not used anymore");
			errorsInTransfer.add("TransferHandler - LocalFileBasedTransfer is not used anymore");
			this.updateStatusAndErrors(this.transferId, "FAILED",errorsInTransfer);
			return;
		}
		else if(this.transferType.compareTo("FileBasedTransfer")==0)fileBasedProcess();	
		else if(this.transferType.compareTo("TreeBasedTransfer")==0)treeBasedProcess();

		refreshPeriodicallyScheduledTransfer();
	}


	public void refreshPeriodicallyScheduledTransfer(){
		//------------only in case of a periodically scheduled---------------------------//
		String idTypeOfSchedule=this.transfer.getTypeOfScheduleId();
		TypeOfSchedule typeOfSchedule = this.dbManager.getPersistenceManager().getObjectById(TypeOfSchedule.class,idTypeOfSchedule );
		String periodicallyScheduledId = typeOfSchedule.getPeriodicallyScheduledId();	
		String retrivedStatus= this.dbManager.getPersistenceManager().getObjectById(Transfer.class,this.transferId).getStatus();


		// if it's periodically scheduled we change (refresh with a new value) the startInstance
		// and we make the status of the transfer STANDBY again
		// but if it's been changed in CANCELED in the meantime we keep it as it is...
		if((periodicallyScheduledId!=null)&&(retrivedStatus!="CANCELED")){
			PeriodicallyScheduled periodicallyScheduled = this.dbManager.getPersistenceManager().getObjectById(PeriodicallyScheduled.class,periodicallyScheduledId );
			Calendar startInstance=null;
			startInstance=Utils.getCalendarBasedOnStringDate(periodicallyScheduled.getStartInstanceString());

			try {					
				FrequencyType frequencyType = periodicallyScheduled.getFrequency();

				if(frequencyType==FrequencyType.perYear)startInstance.add(Calendar.YEAR, 1);
				else if(frequencyType==FrequencyType.perMonth)startInstance.add(Calendar.MONTH, 1);
				else if(frequencyType==FrequencyType.perWeek)startInstance.add(Calendar.DAY_OF_MONTH, 7);
				else if(frequencyType==FrequencyType.perDay)startInstance.add(Calendar.DAY_OF_MONTH, 1);
				else if(frequencyType==FrequencyType.perHour)startInstance.add(Calendar.HOUR_OF_DAY, 1);
				else if(frequencyType==FrequencyType.perMinute)startInstance.add(Calendar.MINUTE, 1);
			}catch (Exception e) {
				logger.error("\nTransferHandler -- Exception in forward the startInstance - "+this.submitter +" - id="+this.transferId);
				e.printStackTrace();
			}

			try{				
				//i don't use the startInstance variable because it creates a problem when trying to store it in db 
				//it seems that there's an issue after using the 'roll' method.. that's why we copy
				//the values that we want in another calendar variable
				//----fixed .. I just use add instead of roll 
				Calendar tmpCalendar=(Calendar) startInstance.clone();
				tmpCalendar.set(Calendar.YEAR,startInstance.get(Calendar.YEAR));
				tmpCalendar.set(Calendar.MONTH,startInstance.get(Calendar.MONTH));
				tmpCalendar.set(Calendar.DAY_OF_MONTH,startInstance.get(Calendar.DAY_OF_MONTH));
				tmpCalendar.set(Calendar.HOUR_OF_DAY,startInstance.get(Calendar.HOUR_OF_DAY));
				tmpCalendar.set(Calendar.MINUTE,startInstance.get(Calendar.MINUTE));

				//this.dbManager.updateTransferStartInstance(this.transferId, tmpCalendar);
				dbManager.updateTransferStartInstanceString(transferId, Utils.getFormattedCalendarString(tmpCalendar));

			}catch (Exception e) {
				logger.error("\nCheckForTransfers -- Exception in updating the transfer startInstance (2)- "+this.submitter +" - id="+this.transferId);
				e.printStackTrace();
			}

			this.updateStatusAndErrors(this.transferId, "STANDBY",null);
		}
	}


	public void addActiveTransferResource(){
		String[] alreadyActiveTransferIDs = this.resource.getActiveTransfers();
		String[] newActiveTransferIDS= new String[alreadyActiveTransferIDs.length+1];
		int i=0;
		for(String temp : alreadyActiveTransferIDs){
			newActiveTransferIDS[i]=temp;
			i++;
		}
		newActiveTransferIDS[i]=this.transferId;
		try{
			this.resource.setActiveTransfers(newActiveTransferIDS);
			this.resource.store();
		}catch (Exception e) {
			logger.error("TransferHandler (addResourceOfActiveTransfer)-- Exception in Storing the Resource Property 'ActiveTransfers'"  );
			e.printStackTrace();
		}
		try{
			int alreadyNumberOfActive = Integer.valueOf(this.resource.getNumOfActiveTransfers());
			String newNumberOfActive = (alreadyNumberOfActive+1)+"";
			this.resource.setNumOfActiveTransfers(newNumberOfActive);
			this.resource.store();
		}catch (Exception e) {
			logger.error("TransferHandler (addResourceOfActiveTransfer) -- Exception in Storing the Resource Property 'NumOfActiveTransfers'"  );
			e.printStackTrace();
		}
	}

	public void removeActiveTransferResource(){
		String[] alreadyActiveTransferIDs = this.resource.getActiveTransfers();
		String[] newActiveTransferIDS= new String[alreadyActiveTransferIDs.length-1];
		int i=0;
		for(String temp : alreadyActiveTransferIDs){
			if(this.transferId.compareTo(temp)==0)continue;
			newActiveTransferIDS[i]=temp;
			i++;
		}
		try{
			this.resource.setActiveTransfers(newActiveTransferIDS);
			this.resource.store();
		}catch (Exception e) {
			logger.error("TransferHandler (removeActiveTransferResource)-- Exception in Storing the Resource Property 'ActiveTransfers'"  );
			e.printStackTrace();
		}
		try{
			int alreadyNumberOfActive = Integer.valueOf(this.resource.getNumOfActiveTransfers());
			String newNumberOfActive = (alreadyNumberOfActive-1)+"";
			this.resource.setNumOfActiveTransfers(newNumberOfActive);
			this.resource.store();
		}catch (Exception e) {
			logger.error("TransferHandler (removeActiveTransferResource) -- Exception in Storing the Resource Property 'NumOfActiveTransfers'"  );
			e.printStackTrace();
		}
	}

	public void sleepSixSec(){
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			logger.error("\nTransferHandler (sleepFiveSec)-- InterruptedException-Unable to sleep");
			e.printStackTrace();
		}
	}

	public void updateStatusAndErrors(String transferId, String status, List<String> errorsInTransfer){
		// updating the transfer status
		try	{ 
			ServiceContext.getContext().getDbManager().updateTransferStatus(this.transferId,status);
		}
		catch(Exception e){
			logger.error("TransferHandler "+
					"- Exception in changing the transfer status\n");
			e.printStackTrace();
		}
		// updating the transfer errors if exist any
		if(errorsInTransfer!=null){
			try {					
				this.dbManager.updateTransferError(this.transferId, errorsInTransfer);
			}catch (Exception e) {
				logger.error("TransferHandler "+
						"- Exception in updating the transfer errors\n");
				e.printStackTrace();
			}
		}		

		if(status.compareTo("STANDBY")!=0&&status.compareTo("QUEUED")!=0){
			//remove the transfer id from the resource array of active transfers
			removeActiveTransferResource();
			//update the agent statistics
			updateAgentStatistics(status);
		}
	}

	public void updateAgentStatistics(String status){
		// updating the agent statistics
		if(agent==null)return;
		String agentIdOfIS=agent.getAgentIdOfIS();
		//retrieving the agentStatictics by checking first if exists
		AgentStatistics stats=null;		
		Extent<?> resultExtent = this.dbManager.getPersistenceManager().getExtent(AgentStatistics.class, true);
		Iterator<?> iter = resultExtent.iterator();
		boolean flagExists=false;
		while (iter.hasNext()){
			AgentStatistics obj=(AgentStatistics)iter.next();
			if(obj.getAgentIdOfIS().compareTo(agentIdOfIS)==0){
				stats=obj;
				flagExists=true;
				break;
			}
		}
		if(flagExists==false)return; 
		//else ... 
		String id = stats.getAgentStatisticsId();
		int ongoing = stats.getOngoingTransfers();
		int failed = stats.getFailedTransfers();
		int succeeded = stats.getSucceededTransfers();
		int canceled = stats.getCanceledTransfers();
		int total = stats.getTotalFinishedTransfers();

		if     (status.compareTo("COMPLETED")==0){--ongoing; ++succeeded; ++total;}
		else if(status.compareTo("COMPLETED_WITH_ERRORS")==0){--ongoing; ++succeeded; ++total;}
		else if(status.compareTo("FAILED")==0){--ongoing; ++failed; ++total;}
		else if(status.compareTo("CANCELED")==0){--ongoing; ++canceled; ++total;}
		else if(status.compareTo("ONGOING")==0){++ongoing;}

		try {
			this.dbManager.updateAgentStatistics(id, ongoing, failed, succeeded, canceled, total);
		} catch (Exception e) {
			logger.error("TransferHandler "+
					"- Exception in updating the agent Statistics\n");
			e.printStackTrace();
		}
	}

	public void fileBasedProcess(){
		//update start time
		updateStartTimeInTransfer();

		String transferIdOfAgent = null;
		//objectsToBeTransferred is used only when we don't have source node ..
		List<TransferObject> objectsToBeTransferred= new ArrayList<TransferObject>();

		//### - CHANGED
		// ### SourceId is always null it means that we don't have any source node
		// we use directly the urls from the transfer objects 

		//we need to collect the right TransferObjects
		//check for the objects having the same trasferId with this one.
		Query query=null;
		List<TransferObject> list=null;

		//checking for the DB if it has finished storing the objs
		//if it has not in 2 minutes the transfer stops and the status becomes "FAILED"
		this.transfer=this.dbManager.getPersistenceManager().getObjectById(Transfer.class, this.transferId);
		boolean flagReadyObjs= this.transfer.isReadyObjects();				
		int numSleeps=0;
		while(!flagReadyObjs){
			logger.debug("TransferHandler -- DB has not finished storing the objs.. sec="+numSleeps*6);
			sleepSixSec();
			this.transfer=this.dbManager.getPersistenceManager().getObjectById(Transfer.class, this.transferId);
			flagReadyObjs= this.transfer.isReadyObjects();
			numSleeps++;
			if(numSleeps>20){
				logger.debug("TransferHandler -- DB did not store the objs in 120s .. transfer aborted-failed..");
				this.updateStatusAndErrors(this.transferId, "FAILED", null);
				//we do not proceed to this transfer.. instead we check if it is 
				//periodical in order to refresh its next time instance for transfering
				refreshPeriodicallyScheduledTransfer();
				return;
			}
		}

		query = ServiceContext.getContext().getDbManager().getPersistenceManager().newQuery(TransferObject.class);
		list = (List<TransferObject>) query.execute();

		String transferIdOfSpecific;

		logger.debug("TransferHandler -- listOfObjects="+list.size()+" -- transferId="+this.transferId);

		for(TransferObject obj : list){
			transferIdOfSpecific = obj.getTransferid();
			if(transferIdOfSpecific==null){
				logger.debug("some transfer object has null transfer id - we skip it ");
				continue;
			}
			if(transferIdOfSpecific.compareTo(this.transferId)==0){
				objectsToBeTransferred.add(obj); 				
			}
		}
		logger.debug("TransferHandler -- listOfObjectsToBeTransferrwes="+objectsToBeTransferred.size());

		//first needed input for the agent method
		ArrayList<URI> inputUris= new ArrayList<URI>();				
		for(TransferObject obj : objectsToBeTransferred){			
			try {				
				inputUris.add(new URI(obj.getSrcURI()));
			} catch (URISyntaxException  e) {
				e.printStackTrace();
			}
		}	
		//optional print
		String inp="";
		for(URI tmp:inputUris)inp=tmp+"\n";
				logger.debug("TransferHandler - inputURIs:\n"+inp);

				storageType typeOfStorage=null;
				StorageManagerDetails storageManagerDetails = new StorageManagerDetails();

				// if StorageId is null it means that we don't have any storage node
				// we use the agent's node as a storage
				if(this.transfer.getTypeOfStorage().compareTo(storageType.LocalGHN.name())==0){					
					typeOfStorage=storageType.LocalGHN;
					logger.debug("TransferHandler -- storageType=LocalGHN");
					//second needed input for the agent method
					String destinationFolder = this.transfer.getDestinationFolder();

					//third input for the agent method
					TransferOptions transferOptions= new TransferOptions();				
					transferOptions.setType(typeOfStorage);
					transferOptions.setOverwriteFile(this.transfer.isOverwrite());
					transferOptions.setUnzipFile(this.transfer.isUnzipFile());
					transferOptions.setStorageManagerDetails(storageManagerDetails);

					String scope = this.transfer.getScope();
					logger.debug("TransferHandler -- scooooooooope="+scope);

					try {
						//GCUBEScopeManager.DEFAULT.setScope(GCUBEScope.getScope(scope));
						ScopeProvider.instance.set(scope); 

						agentLibrary =  transferAgent().at(hostAgent, portAgent).build();	
						logger.debug("TransferHandler - print inputUris before call:");
						for(URI tmp:inputUris)logger.debug(tmp);
								// *** startTransfer *** //
								transferIdOfAgent = agentLibrary.startTransfer(inputUris, destinationFolder, transferOptions);
								logger.debug("TransferHandler - After the agentLibrary.startTransfer(...)");
					} catch (Exception e) {
						logger.error("TransferHandler - Exception when call agentLibrary.startTransfer(.....)");
						errorsInTransfer.add(e.getMessage());
						failed=true;
						e.printStackTrace();
					}
				}
				else if(this.transfer.getTypeOfStorage().compareTo(storageType.StorageManager.name())==0){

					// now storageManagerDetails has to be filled with the real info
					DataStorage storage = null;
					String storageId = this.transfer.getStorageId();
					//retrieving the data storage by checking first if it exists 
					Extent<?> resultExtent = this.dbManager.getPersistenceManager().getExtent(DataStorage.class, true);
					Iterator<?> iter = resultExtent.iterator();
					boolean flagExists=false;
					while (iter.hasNext()){
						DataStorage obj=(DataStorage)iter.next();
						if(obj.getDataStorageId().compareTo(storageId)==0){
							storage=obj;
							flagExists=true;
							break;
						}
					}
					if(flagExists==false){
						logger.error("TransferHandler - Error - datastorage with id="+storageId+" does not exist in DB anymore");
						errorsInTransfer.add("TransferHandler - Error - datastorage with id="+storageId+" does not exist in DB anymore");
						this.updateStatusAndErrors(this.transferId, "FAILED",errorsInTransfer);
						return;
					}	

					typeOfStorage=storageType.StorageManager;
					logger.debug("TransferHandler -- storageType=StorageManager");
					storageManagerDetails.setOwner(storage.getOwner());
					storageManagerDetails.setServiceName(storage.getServiceName());
					storageManagerDetails.setServiceClass(storage.getServiceClass());
					storageManagerDetails.setAccessType(storageAccessType.valueOf(storage.getAccessType()));

					//second needed input for the agent method
					String destinationFolder = this.transfer.getDestinationFolder();

					//third input for the agent method
					TransferOptions transferOptions= new TransferOptions();				
					transferOptions.setType(typeOfStorage);
					transferOptions.setOverwriteFile(this.transfer.isOverwrite());
					transferOptions.setUnzipFile(this.transfer.isUnzipFile());
					transferOptions.setStorageManagerDetails(storageManagerDetails);

					String scope = this.transfer.getScope();
					logger.debug("TransferHandler -- scooooooooope="+scope);

					try {
						//GCUBEScopeManager.DEFAULT.setScope(GCUBEScope.getScope(scope));
						ScopeProvider.instance.set(scope); 

						agentLibrary =  transferAgent().at(hostAgent, portAgent).build();	
						logger.debug("TransferHandler - print inputUris before call:");
						for(URI tmp:inputUris)logger.debug(tmp);
								// *** startTransfer *** //
								transferIdOfAgent = agentLibrary.startTransfer(inputUris, destinationFolder, transferOptions);
								logger.debug("TransferHandler - After the agentLibrary.startTransfer(...)");
					} catch (Exception e) {
						logger.error("TransferHandler - Exception when call agentLibrary.startTransfer(.....)");
						errorsInTransfer.add(e.getMessage());
						failed=true;
						e.printStackTrace();
					}
				}
				else if(this.transfer.getTypeOfStorage().compareTo(storageType.DataStorage.name())==0){

					typeOfStorage=storageType.DataStorage;
					logger.debug("TransferHandler -- storageType=DataStorage");
					//second needed input for the agent method
					ArrayList<URI> outputURIs=new ArrayList<URI>();
					for(TransferObject obj : objectsToBeTransferred){			
						try {
							outputURIs.add(new URI(obj.getDestUri()));
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
					}	

					//third input for the agent method
					TransferOptions transferOptions= new TransferOptions();				
					transferOptions.setType(typeOfStorage);
					transferOptions.setOverwriteFile(this.transfer.isOverwrite());
					transferOptions.setUnzipFile(this.transfer.isUnzipFile());
					transferOptions.setStorageManagerDetails(storageManagerDetails);

					String scope = this.transfer.getScope();
					logger.debug("TransferHandler -- scooooooooope="+scope);

					try {
						//GCUBEScopeManager.DEFAULT.setScope(GCUBEScope.getScope(scope));
						ScopeProvider.instance.set(scope); 

						agentLibrary =  transferAgent().at(hostAgent, portAgent).build();	
						logger.debug("TransferHandler - print inputUris before call:");
						for(URI tmp:inputUris)logger.debug(tmp);
								// *** startTransfer *** //
								transferIdOfAgent = agentLibrary.startTransfer(inputUris, outputURIs, transferOptions);
								logger.debug("TransferHandler - After the agentLibrary.startTransfer(...)");
					} catch (Exception e) {
						logger.error("TransferHandler - Exception when call agentLibrary.startTransfer(inputUris, outputURIs, transferOptions)");
						errorsInTransfer.add(e.getMessage());
						failed=true;
						e.printStackTrace();
					}
				}

				if(transferIdOfAgent==null){
					logger.debug("TransferHandler - transferIdOfAgent==null - transfer becomes FAILED");
					this.updateStatusAndErrors(this.transferId, "FAILED",null);
					//we do not proceed to this transfer.. instead we check if it is 
					//periodical in order to refresh its next time instance for transfering
					refreshPeriodicallyScheduledTransfer();
					return;
				}
				try	{ // *** store the transferIdOfAgent inside the Transfer in DB ***
					ServiceContext.getContext().getDbManager().updateTransferIdOfAgentInTransfer(this.transferId,transferIdOfAgent);
				}
				catch(Exception e){
					logger.error("TransferHandler "+
							"- Exception in storing the transferIdOfAgent inside the Transfer in DB\n");
					e.printStackTrace();
				}

				//after checking if we have or not SourceData node / DataStorage node
				//and calling the appropriate operation of agent, 
				//we continuously monitor the result until we get sth different to "STARTED"

				String statusResult="";
				MonitorTransferReportMessage monitorRes = null;
				boolean flagCont=true;
				int limit = 10;
				int num=0;
				long time=0;

				do{	
					try{
						// *** monitorTransfer *** //
						monitorRes = agentLibrary.monitorTransferWithProgress(transferIdOfAgent);
						if(monitorRes!=null){
							statusResult=monitorRes.getTransferStatus();
							logger.debug("TransferHandler - After the agentLibrary.monitorTransferWithProgress(...) - statusResult="+statusResult + " - totalBytes="+monitorRes.getTotalBytes()+" - transferredBytes="+monitorRes.getBytesTransferred()+"  ********************");
						}
						else {
							statusResult=agentLibrary.monitorTransfer(transferIdOfAgent);
							logger.debug("TransferHandler - monitorTransferWithProgress was null! .. After the agentLibrary.monitorTransfer(...) - statusResult="+statusResult +"  ********************");
						}
					} catch (Exception e) {
						logger.error("TransferHandler - Exception when call agentLibrary.monitorTransfer(.....)");
						errorsInTransfer.add(e.getMessage());
						e.printStackTrace();
						this.updateStatusAndErrors(this.transferId,"FAILED",null);
						//we do not proceed to this transfer.. instead we check if it is 
						//periodical in order to refresh its next time instance for transfering
						refreshPeriodicallyScheduledTransfer();
						return;
					}
					try {
						Thread.sleep(checkForMonitorResultIntervalMS);
					} catch (InterruptedException e) {
						logger.error("TransferHandler -- InterruptedException-Unable to sleep");
						e.printStackTrace();
					}		

					if(statusResult==null){flagCont=true;++num;}         // if result is still null after 'limit' calls we make the transfer FAILED 
					else if (statusResult.compareTo("QUEUED")==0){
						time=time+checkForMonitorResultIntervalMS;
						logger.debug("TransferHandler - QUEUED result: time="+time+"MS, maxTime="+maxTimeForMonitoringWithQueuedResultMS+"MS");
						flagCont=true;
					}
					else if (statusResult.compareTo("STARTED")==0){
						flagCont=true;
						//update transfer db objs with the monitor in progress results
						long total_size=0;
						long transferredBytes=0;
						if(monitorRes!=null){
							total_size=monitorRes.getTotalBytes();
							transferredBytes= monitorRes.getBytesTransferred();
						}
						try {
							dbManager.updateTransferBytes(this.transferId, total_size, transferredBytes);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else flagCont=false;

				}while(flagCont && num<=limit && time<=maxTimeForMonitoringWithQueuedResultMS);

				if(statusResult==null){
					logger.debug("TransferHandler -- monitorTransfer result was still null after "+limit+" calls, the transfer becomes FAILED");
					this.updateStatusAndErrors(this.transferId,"FAILED",null);
					//we do not proceed to this transfer.. instead we check if it is 
					//periodical in order to refresh its next time instance for transfering
					refreshPeriodicallyScheduledTransfer();
					return;
				}
				else if (statusResult.compareTo("QUEUED")==0){
					logger.debug("TransferHandler -- monitorTransfer result was still QUEUED after "+maxTimeForMonitoringWithQueuedResultMS+" MS, the transfer becomes FAILED");
					this.updateStatusAndErrors(this.transferId,"FAILED",null);
					//we do not proceed to this transfer.. instead we check if it is 
					//periodical in order to refresh its next time instance for transfering
					refreshPeriodicallyScheduledTransfer();
					return;
				}

				logger.debug("TransferHandler -- monitorTransfer result="+statusResult);
				String retrivedStatus= this.dbManager.getPersistenceManager().getObjectById(Transfer.class,this.transferId).getStatus();

				//check the status of the transfer
				//if we had a cancellation during the transfer: 
				//Although transfer has been done we keep the status "CANCELED"
				//in case of having a periodicallyScheduled transfer.
				if(retrivedStatus.compareTo("CANCELED")==0){
					logger.debug("TransferHandler -- Canceled");
					this.updateStatusAndErrors(this.transferId, "CANCELED",null);
					//we do not proceed to this transfer.. instead we check if it is 
					//periodical in order to refresh its next time instance for transfering
					refreshPeriodicallyScheduledTransfer();
					return;
				}

				//check the result of monitoring - if it is cancel we update the status and return
				if(statusResult.compareTo("CANCEL")==0){
					logger.debug("TransferHandler -- Canceled - "+this.submitter+" - id="+this.transferId);			
					this.updateStatusAndErrors(this.transferId, "CANCELED", null);
					//we do not proceed to this transfer.. instead we check if it is 
					//periodical in order to refresh its next time instance for transfering
					refreshPeriodicallyScheduledTransfer();
					return;
				}

				//*** getTransferOutcomes *** //
				ArrayList<FileTransferOutcome> outcomes=null;
				try {
					outcomes = agentLibrary.getTransferOutcomes(transferIdOfAgent, FileTransferOutcome.class);			
				} catch (Exception e) {
					logger.error("TransferHandler - Exception when call agentLibrary.getTransferOutcomes(..) ");
					errorsInTransfer.add(e.getMessage());
					e.printStackTrace();
				}
				long total_size=0;
				long transferredBytes=0;
				List<String> listOutcomes=new ArrayList<String>();
				if(outcomes!=null){
					try{
						int numOfObj=0;
						List<String> tmpOutcomes=new ArrayList<String>();
						for (FileTransferOutcome outcome : outcomes){
							TransferOutcome transferOutcome = new TransferOutcome();
							String outcomeId = uuidgen.nextUUID();
							transferOutcome.setTransferOutcomesId(outcomeId);
							transferOutcome.setTransferId(transferId);
							transferOutcome.setSubmittedDateOfTransfer(this.transfer.getSubmittedDate());
							transferOutcome.setFileName(outcome.getFilename());
							transferOutcome.setException(outcome.getException());
							transferOutcome.setFailure(outcome.isFailure());
							transferOutcome.setSuccess(outcome.isSuccess());
							transferOutcome.setDest(outcome.getDest());
							transferOutcome.setTransferTime(outcome.getTransferTime()+"");
							transferOutcome.setTransferredBytesOfObj(outcome.getTransferredBytes().toString());
							transferOutcome.setSize(outcome.getTotal_size().toString());

							if(outcome.getTotal_size()!=null)total_size=total_size + outcome.getTotal_size();
							if(outcome.getTransferredBytes()!=null)transferredBytes=transferredBytes + outcome.getTransferredBytes();


							transferOutcome.setNumberOfOutcomeInThisTransfer(numOfObj);
							this.dbManager.storeTransferOutcomes(transferOutcome);

							if(numOfObj==0)logger.debug("TransferHandler -- getTransferOutcomes:");
							logger.debug("Exception: "+outcome.getException());
							logger.debug("FileName: "+ outcome.getFilename());
							logger.debug("Success?: "+ outcome.isSuccess());
							logger.debug("Failure?: "+ outcome.isFailure());
							logger.debug("Size: "+ outcome.getTotal_size());
							logger.debug("Transferred Bytes Of Obj: "+ outcome.getTransferredBytes());

							//objectFailedIDs and objectTrasferredIDs are only stored when we do not have any SourceNode and we use 
							// inputUrls directly .. 
							if(this.transfer.getSourceId()==null){
								if(outcome.isFailure()==true){
									if(objectsToBeTransferred==null || objectsToBeTransferred.get(numOfObj)==null || objectsToBeTransferred.get(numOfObj).getObjectId()==null);//nothinh
									else objectFailedIDs.add(objectsToBeTransferred.get(numOfObj).getObjectId());			
								}
								else if (outcome.isSuccess()==true){
									if(objectsToBeTransferred==null || objectsToBeTransferred.get(numOfObj)==null || objectsToBeTransferred.get(numOfObj).getObjectId()==null);//nothing
									else objectTrasferredIDs.add(objectsToBeTransferred.get(numOfObj).getObjectId());			
								}
							}
							tmpOutcomes.add(outcomeId);
							numOfObj++;
						}
						
						//APPEND OUTCOMES
						String[] arrayOutcomes=this.transfer.getOutcomes();
						if(arrayOutcomes!=null){
							for(String tmp:arrayOutcomes)listOutcomes.add(tmp);
						}
						
						listOutcomes.addAll(tmpOutcomes);
					//	String[] arrayToBeStored=listOutcomes.toArray(new String[listOutcomes.size()]);

					} catch (Exception e) {
						logger.error("TransferHandler - Exception when call dbManager.storeTransferOutcomes");
						errorsInTransfer.add(e.getMessage());
						e.printStackTrace();
					}	
				}
				
				try{
					this.dbManager.updateOutcomesInTransfer(transferId, listOutcomes);
				} catch (Exception e) {
					logger.error("TransferHandler - Exception when call dbManager.updateOutcomesInTransfer ");
					errorsInTransfer.add(e.getMessage());
					e.printStackTrace();
				}		
				//updating transferred bytes
				try {
					dbManager.updateTransferBytes(transfer.getTransferId(), total_size, transferredBytes);
				} catch (Exception e) {
					logger.error("TransferHandler - Exception when call dbManager.updateTransferBytes ");
					errorsInTransfer.add(e.getMessage());
					e.printStackTrace();
				}

				if(statusResult.compareTo("DONE")==0 ||
						statusResult.compareTo("DONE_WITH_ERRORS")==0){
					String status=null;
					try {					
						if(statusResult.compareTo("DONE")==0)status="COMPLETED";
						else status="COMPLETED_WITH_ERRORS";

						this.updateStatusAndErrors(this.transferId, status,null);
						logger.debug("TransferHandler -- "+status+" - "+this.submitter+" - id="+this.transferId);			

						//objectFailedIDs and objectTrasferredIDs are only stored when we do not have any SourceNode and we use 
						// inputUrls directly .. 
						if(this.transfer.getSourceId()==null){
							String[] objectFailedIDsarray;
							String[] objectTrasferredIDsarray;
							if(objectFailedIDs==null || objectFailedIDs.size()==0)objectFailedIDsarray=new String[]{""};
							else objectFailedIDsarray=objectFailedIDs.toArray(new String[objectFailedIDs.size()]);

							if(objectTrasferredIDs==null || objectTrasferredIDs.size()==0)objectTrasferredIDsarray=new String[]{""};
							else objectTrasferredIDsarray=objectTrasferredIDs.toArray(new String[objectTrasferredIDs.size()]);
							
							this.dbManager.updateObjectTrasferredIDs(this.transferId, objectTrasferredIDsarray);
							this.dbManager.updateObjectFailedIDs(this.transferId, objectFailedIDsarray);
						}
					}catch (Exception e) {
						logger.error("TransferHandler -- If "+status+" - Exception");
						e.printStackTrace();
					}

					//optional printing when we don't have Source node and we use directly inputUrls
					if(this.transfer.getSourceId()==null){
						for(String objId : this.transfer.getObjectTrasferredIDs())
							logger.debug("TransferHandler -- Completed - TransferredObjectID:"+objId);

								for(String objId : this.transfer.getObjectFailedIDs())
									logger.debug("TransferHandler -- Completed - FailedObjectID:"+objId);
					}
				}
				else if(statusResult.compareTo("FAILED")==0){
					this.updateStatusAndErrors(this.transferId, "FAILED", errorsInTransfer);
					logger.debug("TransferHandler -- Failed - "+this.submitter+" - id="+this.transferId);
					//we do not proceed to this transfer.. instead we check if it is 
					//periodical in order to refresh its next time instance for transfering
					refreshPeriodicallyScheduledTransfer();
					return;
				}

	}

	public void treeBasedProcess(){
		//update start time
		updateStartTimeInTransfer();

		String transferIdOfAgent = null;

		Query query=null;

		this.transfer=this.dbManager.getPersistenceManager().getObjectById(Transfer.class, this.transferId);
		String inputSourceID=transfer.getSourceId();
		String outputStorageId=transfer.getStorageId();
		
		String patternInputString= transfer.getPattern();
		XStream xstreamForPattern = new XStream();
		Pattern patternInput = 	(Pattern) xstreamForPattern.fromXML(patternInputString);

		String scope = transfer.getScope();
		try {
			ScopeProvider.instance.set(scope); 

			agentLibrary =  transferAgent().at(hostAgent, portAgent).build();	
			// *** startTransfer *** //
			transferIdOfAgent = agentLibrary.startTransfer(patternInput, inputSourceID, outputStorageId);
			logger.debug("TransferHandler - After the agentLibrary.startTransfer(...)");
		} catch (Exception e) {
			logger.error("TransferHandler - Exception when call agentLibrary.startTransfer(.....)");
			errorsInTransfer.add(e.getMessage());
			failed=true;
			e.printStackTrace();
		}


		if(transferIdOfAgent==null){
			logger.debug("TransferHandler - transferIdOfAgent==null - transfer becomes FAILED");
			this.updateStatusAndErrors(this.transferId, "FAILED",null);
			//we do not proceed to this transfer.. instead we check if it is 
			//periodical in order to refresh its next time instance for transfering
			refreshPeriodicallyScheduledTransfer();
			return;
		}
		try	{ // *** store the transferIdOfAgent inside the Transfer in DB ***
			ServiceContext.getContext().getDbManager().updateTransferIdOfAgentInTransfer(this.transferId,transferIdOfAgent);
		}
		catch(Exception e){
			logger.error("TransferHandler "+
					"- Exception in storing the transferIdOfAgent inside the Transfer in DB\n");
			e.printStackTrace();
		}

		//after checking if we have or not SourceData node / DataStorage node
		//and calling the appropriate operation of agent, 
		//we continuously monitor the result until we get sth different to "STARTED"

		String statusResult="";
		MonitorTransferReportMessage monitorRes = null;
		boolean flagCont=true;
		int limit = 10;
		int num=0;
		long time=0;

		do{	
			try{
				// *** monitorTransfer *** //
				//monitorRes = agentLibrary.monitorTransferWithProgress(transferIdOfAgent);
				//if(monitorRes!=null){
				//	statusResult=monitorRes.getTransferStatus();
				//	logger.debug("TransferHandler - After the agentLibrary.monitorTransferWithProgress(...) - statusResult="+statusResult + " - totalBytes="+monitorRes.getTotalBytes()+" - transferredBytes="+monitorRes.getBytesTransferred()+"  ********************");
				//}
				//else {
				statusResult=agentLibrary.monitorTransfer(transferIdOfAgent);
				logger.debug("TransferHandler - monitorTransfer - status="+statusResult);
				//	logger.debug("TransferHandler - monitorTransferWithProgress was null! .. After the agentLibrary.monitorTransfer(...) - statusResult="+statusResult +"  ********************");
				//}
			} catch (Exception e) {
				logger.error("TransferHandler - Exception when call agentLibrary.monitorTransfer(.....)");
				errorsInTransfer.add(e.getMessage());
				e.printStackTrace();
				this.updateStatusAndErrors(this.transferId,"FAILED",null);
				//we do not proceed to this transfer.. instead we check if it is 
				//periodical in order to refresh its next time instance for transfering
				refreshPeriodicallyScheduledTransfer();
				return;
			}
			try {
				Thread.sleep(checkForMonitorResultIntervalMS);
			} catch (InterruptedException e) {
				logger.error("TransferHandler -- InterruptedException-Unable to sleep");
				e.printStackTrace();
			}		

			if(statusResult==null){flagCont=true;++num;}         // if result is still null after 'limit' calls we make the transfer FAILED 
			else if (statusResult.compareTo("QUEUED")==0){
				time=time+checkForMonitorResultIntervalMS;
				logger.debug("TransferHandler - QUEUED result: time="+time+"MS, maxTime="+maxTimeForMonitoringWithQueuedResultMS+"MS");
				flagCont=true;
			}
			else if (statusResult.compareTo("STARTED")==0){
				flagCont=true;
				//update transfer db objs with the monitor in progress results
				//				long total_size=0;
				//				long transferredBytes=0;
				//				if(monitorRes!=null){
				//					total_size=monitorRes.getTotalBytes();
				//					transferredBytes= monitorRes.getBytesTransferred();
				//				}
				//				try {
				//					dbManager.updateTransferBytes(this.transferId, total_size, transferredBytes);
				//				} catch (Exception e) {
				//					e.printStackTrace();
				//				}
			}
			else flagCont=false;

		}while(flagCont && num<=limit && time<=maxTimeForMonitoringWithQueuedResultMS);

		if(statusResult==null){
			logger.debug("TransferHandler -- monitorTransfer result was still null after "+limit+" calls, the transfer becomes FAILED");
			this.updateStatusAndErrors(this.transferId,"FAILED",null);
			//we do not proceed to this transfer.. instead we check if it is 
			//periodical in order to refresh its next time instance for transfering
			refreshPeriodicallyScheduledTransfer();
			return;
		}
		else if (statusResult.compareTo("QUEUED")==0){
			logger.debug("TransferHandler -- monitorTransfer result was still QUEUED after "+maxTimeForMonitoringWithQueuedResultMS+" MS, the transfer becomes FAILED");
			this.updateStatusAndErrors(this.transferId,"FAILED",null);
			//we do not proceed to this transfer.. instead we check if it is 
			//periodical in order to refresh its next time instance for transfering
			refreshPeriodicallyScheduledTransfer();
			return;
		}

		logger.debug("TransferHandler -- monitorTransfer result="+statusResult);
		String retrivedStatus= this.dbManager.getPersistenceManager().getObjectById(Transfer.class,this.transferId).getStatus();

		//check the status of the transfer
		//if we had a cancellation during the transfer: 
		//Although transfer has been done we keep the status "CANCELED"
		//in case of having a periodicallyScheduled transfer.
		if(retrivedStatus.compareTo("CANCELED")==0){
			logger.debug("TransferHandler -- Canceled");
			this.updateStatusAndErrors(this.transferId, "CANCELED",null);
			//we do not proceed to this transfer.. instead we check if it is 
			//periodical in order to refresh its next time instance for transfering
		//	refreshPeriodicallyScheduledTransfer();
			return;
		}

		//check the result of monitoring - if it is cancel we update the status and return
		if(statusResult.compareTo("CANCEL")==0){
			logger.debug("TransferHandler -- Canceled - "+this.submitter+" - id="+this.transferId);			
			this.updateStatusAndErrors(this.transferId, "CANCELED", null);
			//we do not proceed to this transfer.. instead we check if it is 
			//periodical in order to refresh its next time instance for transfering
		//	refreshPeriodicallyScheduledTransfer();
			return;
		}

		//*** getTransferOutcomes *** //
		ArrayList<TreeTransferOutcome> outcomes=null;
		try {
			outcomes = agentLibrary.getTransferOutcomes(transferIdOfAgent, TreeTransferOutcome.class);			
		} catch (Exception e) {
			logger.error("TransferHandler - Exception when call agentLibrary.getTransferOutcomes(..) ");
			errorsInTransfer.add(e.getMessage());
			e.printStackTrace();
		}
		
		//list of outcomes should contain only one obj... (Tree Outcome case - we do not keep for eash tree but for the whole transfer)
		if(outcomes==null){
			logger.error("TransferHandler - treeoutcome list size is null ...  ");
			errorsInTransfer.add("TransferHandler - treeoutcome list size is null ...  ");
		}
		else{
			if(outcomes.size()>1)logger.debug("TransferHandler - treeoutcome list size > 1 ... must be 1.. we're taking the first one");
			
			TreeTransferOutcome treeOutcome = new TreeTransferOutcome();
			treeOutcome = outcomes.get(0);
			try{
				//APPEND OUTCOME
				this.dbManager.updateTreeOutcomeInTransfer(transferId, treeOutcome.getException(),treeOutcome.getTotalReadTrees(), treeOutcome.getTotalWrittenTrees());
				
			} catch (Exception e) {
				logger.error("TransferHandler - Exception when call dbManager.updateOutcomesInTransfer ");
				errorsInTransfer.add(e.getMessage());
				e.printStackTrace();
			}		
		}
		
		if(statusResult.compareTo("DONE")==0 ||
				statusResult.compareTo("DONE_WITH_ERRORS")==0){
			String status=null;
			try {					
				if(statusResult.compareTo("DONE")==0)status="COMPLETED";
				else status="COMPLETED_WITH_ERRORS";

				this.updateStatusAndErrors(this.transferId, status,null);
				//in the tree based transfer we fake it ... 1 byte has been transfered just for showing that the progress
				//in the portlet is 100%
				ServiceContext.getContext().getDbManager().updateTransferBytes(transferId, 1, 1);
				logger.debug("TransferHandler -- "+status+" - "+this.submitter+" - id="+this.transferId);			
			}catch (Exception e) {
				logger.error("TransferHandler -- If "+status+" - Exception");
				e.printStackTrace();
			}
		}
		else if(statusResult.compareTo("FAILED")==0){
			this.updateStatusAndErrors(this.transferId, "FAILED", errorsInTransfer);
			logger.debug("TransferHandler -- Failed - "+this.submitter+" - id="+this.transferId);
			//we do not proceed to this transfer.. instead we check if it is 
			//periodical in order to refresh its next time instance for transfering
			refreshPeriodicallyScheduledTransfer();
			return;
		}

	}


	public void updateStartTimeInTransfer(){
		long startTime = System.currentTimeMillis();
		try {
			this.dbManager.updateStartTimeInTransfer(transferId, startTime);
		} catch (Exception e) {
			logger.debug("TransferHandler -- Exception when updating the start time ...");
			e.printStackTrace();
		}
	}

}
