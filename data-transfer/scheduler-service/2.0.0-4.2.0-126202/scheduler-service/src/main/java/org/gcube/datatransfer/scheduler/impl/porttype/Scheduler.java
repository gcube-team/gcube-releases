package org.gcube.datatransfer.scheduler.impl.porttype;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.transferAgent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.Extent;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.agent.Types.AnyHolder;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.gcube.datatransfer.common.scheduler.Types.FrequencyType;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.AgentStatistics;
import org.gcube.datatransfer.scheduler.db.model.DataStorage;
import org.gcube.datatransfer.scheduler.db.model.ManuallyScheduled;
import org.gcube.datatransfer.scheduler.db.model.PeriodicallyScheduled;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.db.model.TransferObject;
import org.gcube.datatransfer.scheduler.db.model.TransferOutcome;
import org.gcube.datatransfer.scheduler.db.model.TransferTreeOutcome;
import org.gcube.datatransfer.scheduler.db.model.TypeOfSchedule;
import org.gcube.datatransfer.scheduler.impl.check.CheckDBForTransfersThread;
import org.gcube.datatransfer.scheduler.impl.context.SchedulerContext;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerResource;
import org.gcube.datatransfer.scheduler.impl.utils.Utils;
import org.gcube.datatransfer.scheduler.is.ISManager;
import org.gcube.datatransfer.scheduler.library.obj.InfoCancelSchedulerMessage;
import org.gcube.datatransfer.scheduler.library.obj.SchedulerObj;
import org.gcube.datatransfer.scheduler.library.outcome.CallingSchedulerResult;
import org.globus.wsrf.ResourceException;

import com.thoughtworks.xstream.XStream;


public class Scheduler extends GCUBEPortType {
	/** The UUIDGen */
	private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();
	GCUBELog logger = new GCUBELog(this);
  
	public DataTransferDBManager dbManager;
	public ISManager isManagerForAgents;
	public ISManager isManagerForSources;
	public ISManager isManagerForStorages;
	
	@Override
	protected ServiceContext getServiceContext() {return ServiceContext.getContext();}

	/*
	 * storeInfoScheduler
	 * input: String InfoSchedulerMessage (StartTransferMessage & TypeOfScheduler)
	 * return: String with the transfer id
	 */
	public String storeInfoScheduler(String msg) throws GCUBEFault {
		
		ServiceContext sctx = ServiceContext.getContext();
		this.dbManager=ServiceContext.getContext().getDbManager();
		this.isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();
		this.isManagerForSources=ServiceContext.getContext().getIsManagerForSources();
		this.isManagerForStorages=ServiceContext.getContext().getIsManagerForStorages();

		SchedulerResource resource=null;
		String nameOfCheckThread=null;
		try {
			//1. **** Retrieve the Resource ..****
			resource = this.getResource();
			nameOfCheckThread=resource.getCheckDBThread();
			logger.debug("Stateful Service(storeInfoScheduler) - Thread name for checking DB:"+resource.getCheckDBThread());

		}catch (Exception e) {
			throw sctx.getDefaultException(e).toFault();
		}


		//2. **** Storing in the DB .. ****
		SchedulerObj schedulerObj= new SchedulerObj();
		String transferId = uuidgen.nextUUID();
		//Create the transfer in DB
		Transfer t = new Transfer();
		t.setTransferId(transferId);
		Set<TransferObject> transferObjects=new HashSet<TransferObject>();

		String tmpMsg=msg;
		tmpMsg.replaceAll("&lt;", "<");
		tmpMsg=tmpMsg.replaceAll("&gt;", ">");

		XStream xstream = new XStream();
		schedulerObj=(SchedulerObj)xstream.fromXML(tmpMsg);

		String submitter=null;
		try {
			//set submitter
			submitter=this.getResource().getName();
			t.setSubmitter(submitter);
		} catch (ResourceException e1) {
			logger.error("Stateful Service(storeInfoScheduler) - Exception in setting the submitter in Transfer:\n");
			e1.printStackTrace();
		}
		//set status
		t.setStatus("STANDBY");

		//set the scope
		try{
			String scope = schedulerObj.getScope();
			t.setScope(scope);
		}
		catch(Exception e){
			logger.error("Stateful Service(storeInfoScheduler) - Exception in taking the scope:\n");
			e.printStackTrace();
		}

		//set submitted date
		t.setSubmittedDate(schedulerObj.getSubmittedDate());

		String hostnameOfAgent = schedulerObj.getAgentHostname();

		//check for the agent in DB
		String checkResultFromDB=null;
		checkResultFromDB=this.isManagerForAgents.checkIfObjExistsInDB_ByHostname(hostnameOfAgent);
		int num=0;
		while(checkResultFromDB==null){
			logger.debug("Stateful Service(storeInfoScheduler) " +
					"- Error!! - there is no agent with hostname= '"+hostnameOfAgent+"' in DB now or it's not 'UP' yet\n");
			return null;
		}
		logger.debug("Stateful Service ..- agent= '"+hostnameOfAgent+"'");
		//set agent
		t.setAgentId(checkResultFromDB);
		t.setAgentHostname(hostnameOfAgent);


		TypeOfSchedule typeOfSchedule = new TypeOfSchedule();		
		typeOfSchedule.setTypeOfScheduleId(transferId.concat("-typeOfSchedule"));

		// ## if Direct Transfer
		if(schedulerObj.getTypeOfSchedule().isDirectedScheduled()==true){
			typeOfSchedule.setDirectedScheduled(true);

			CheckDBForTransfersThread checkDBForTransfersThread = (CheckDBForTransfersThread) getThread(nameOfCheckThread);
			checkDBForTransfersThread.setImmediateCheck(true);

		}// ## if Manual Transfer - a specific instance
		else if(schedulerObj.getTypeOfSchedule().getManuallyScheduled()!=null){
			ManuallyScheduled manuallyScheduled= new ManuallyScheduled();
			manuallyScheduled.setManuallyScheduledId(transferId.concat("-manuallyScheduled"));
			Calendar calendarTmp=schedulerObj.getTypeOfSchedule().getManuallyScheduled().getCalendar();
			Calendar calendarComp=setCalendarComp(calendarTmp);			
			printDates(calendarTmp);

			if(calendarTmp==null)calendarTmp=Calendar.getInstance();
		//	manuallyScheduled.setCalendar(calendarTmp);			
			manuallyScheduled.setCalendarString(Utils.getFormattedCalendarString(calendarTmp));

			CheckDBForTransfersThread checkDBForTransfersThread = (CheckDBForTransfersThread) getThread(nameOfCheckThread);
			long checkForTransfersIntervalMS = checkDBForTransfersThread.getCheckForTransfersIntervalMS();

			long timeThatTransferWillHappen = calendarTmp.getTimeInMillis()-calendarComp.getTimeInMillis();
			logger.debug("\nStateful Service(storeInfoScheduler) - checkForTransfersIntervalMS="+checkForTransfersIntervalMS+" - timeThatTransferWillHappenMS="+timeThatTransferWillHappen);
			//if the transfer is about to happen during the time that the thread is sleeping, we change the time interval
			if(timeThatTransferWillHappen>0 && timeThatTransferWillHappen<=checkForTransfersIntervalMS){
				checkDBForTransfersThread.setCheckForTransfersIntervalMS(timeThatTransferWillHappen);
				logger.debug("\nStateful Service(storeInfoScheduler) - checkForTransfersIntervalMS="+timeThatTransferWillHappen+" (CHANGED)");
			}
			else if(timeThatTransferWillHappen<0){//we check also if the dateInstance is very old .. 
				//finally decide to keep the original date when it's old.. that's why the bellow is in comment
				//manuallyScheduled.setCalendar(calendarComp); //keep the current time
				checkDBForTransfersThread.setImmediateCheck(true);
			}

			try	{ 
				typeOfSchedule.setManuallyScheduledId(transferId.concat("-manuallyScheduled"));
				ServiceContext.getContext().getDbManager().storeManuallyScheduled(manuallyScheduled);
			}
			catch(Exception e){
				logger.error("Stateful Service(storeInfoScheduler) - Exception in storing the ManuallyScheduled:\n");
				e.printStackTrace();
			}


		}// ## if Periodically Transfer - every minute/hour/day/.. etc
		else if(schedulerObj.getTypeOfSchedule().getPeriodicallyScheduled()!=null){
			PeriodicallyScheduled periodicallyScheduled = new PeriodicallyScheduled();
			periodicallyScheduled.setPeriodicallyScheduledId(transferId.concat("-periodicallyScheduled"));
			FrequencyType frequency = schedulerObj.getTypeOfSchedule().getPeriodicallyScheduled().getFrequency();

			if(frequency==null){
				logger.debug("Stateful Service(storeInfoScheduler) " +
						"- Error!! - frequency=null");
				return null;
			}
			logger.debug("Stateful Service(storeInfoScheduler) " +
					"- periodically transfer case - frequency="+frequency.getValue());


			if(frequency.getValue().compareTo(FrequencyType.perMinute.getValue())==0){
				periodicallyScheduled.setFrequency(org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perMinute);
			}
			else if(frequency.getValue().compareTo(FrequencyType.perHour.getValue())==0){
				periodicallyScheduled.setFrequency(org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perHour);
			}
			else if(frequency.getValue().compareTo(FrequencyType.perDay.getValue())==0){
				periodicallyScheduled.setFrequency(org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perDay);
			}
			else if(frequency.getValue().compareTo(FrequencyType.perWeek.getValue())==0){
				periodicallyScheduled.setFrequency(org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perWeek);
			}
			else if(frequency.getValue().compareTo(FrequencyType.perMonth.getValue())==0){
				periodicallyScheduled.setFrequency(org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perMonth);
			}
			else if(frequency.getValue().compareTo(FrequencyType.perYear.getValue())==0){
				periodicallyScheduled.setFrequency(org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType.perYear);
			}
			else{
				logger.debug("Stateful Service(storeInfoScheduler) " +
						"- Error!! - frequency is not one of the available options (perMinute,perHour ..)");
				return null;
			}

			CheckDBForTransfersThread checkDBForTransfersThread = (CheckDBForTransfersThread) getThread(nameOfCheckThread);
			long checkForTransfersIntervalMS = checkDBForTransfersThread.getCheckForTransfersIntervalMS();

			//we check if the startInstance is very old .. if so we replace it with the present
			Calendar calendarTmp=schedulerObj.getTypeOfSchedule().getPeriodicallyScheduled().getStartInstance();		
			Calendar calendarComp=setCalendarComp(calendarTmp);			
			printDates(calendarTmp);

			if(calendarTmp==null)calendarTmp=Calendar.getInstance();
		//	periodicallyScheduled.setStartInstance(schedulerObj.getTypeOfSchedule().getPeriodicallyScheduled().getStartInstance());
			periodicallyScheduled.setStartInstanceString(Utils.getFormattedCalendarString(calendarTmp));

			long timeThatTransferWillHappen = calendarTmp.getTimeInMillis()-calendarComp.getTimeInMillis();
			if(timeThatTransferWillHappen<0 ){
			//	periodicallyScheduled.setStartInstance(calendarComp);
				periodicallyScheduled.setStartInstanceString(Utils.getFormattedCalendarString(calendarComp));
				checkDBForTransfersThread.setImmediateCheck(true);
			}
			else {
			//	periodicallyScheduled.setStartInstance(calendarTmp);
				periodicallyScheduled.setStartInstanceString(Utils.getFormattedCalendarString(calendarTmp));
				
			}

			try	{ 
				typeOfSchedule.setPeriodicallyScheduledId(transferId.concat("-periodicallyScheduled"));
				ServiceContext.getContext().getDbManager().storePeriodicallyScheduled(periodicallyScheduled);
			}
			catch(Exception e){
				logger.error("Stateful Service(storeInfoScheduler) - Exception in storing the PeriodicallyScheduled:\n");
				e.printStackTrace();
			}


			long frequencyInMS = this.frequencyInMS(frequency);
			//if the frequency is less than the interval for checking the DB, 
			//we change the interval for checking the DB
			logger.debug("\nStateful Service(storeInfoScheduler) - checkForTransfersIntervalMS="+checkForTransfersIntervalMS+" - frequencyInMS="+frequencyInMS);
			if(frequencyInMS>0 && frequencyInMS<checkForTransfersIntervalMS){
				checkDBForTransfersThread.setCheckForTransfersIntervalMS(frequencyInMS); 
				logger.debug("\nStateful Service(storeInfoScheduler) - checkForTransfersIntervalMS="+frequencyInMS+" (CHANGED)");
			}
		}

		try	{
			//set typeOfSchedule
			t.setTypeOfScheduleId(transferId.concat("-typeOfSchedule"));
			ServiceContext.getContext().getDbManager().storeTypeOfSchedule(typeOfSchedule);
		}
		catch(Exception e){
			logger.error("Stateful Service(storeInfoScheduler) - Exception in storing the TypeOfSchedule:\n");
			e.printStackTrace();
		}

		//set transferType
		String transferType= schedulerObj.getTypeOfTransfer();
		t.setTransferType(transferType);

		String[] inputURIs=null;

		// **** LocalFileBasedTransfer removed ****

		// **** If FileBasedTransfer **** 
		if (transferType.compareTo("FileBasedTransfer")==0){
			//several types here... 
			//first urls[] or dataSource to the agent's node
			// second urls[] or dataSource to the Storage Manager
			// and urls[] or dataSource to a remote node

			storageType typeOfStorage= schedulerObj.getTypeOfStorage();
			String destinationFolder = schedulerObj.getDestinationFolder();	
			boolean overwrite = schedulerObj.isOverwrite();
			boolean unzipFile = schedulerObj.isUnzipFile();
			inputURIs = schedulerObj.getInputUrls();	


			// if inputURIs is not null it means that we don't have any source node
			// we use directly the urls.. 
			if(inputURIs!=null){
				//optional print
				String inp="";
				for(String tmp:inputURIs)inp=tmp+"\n";
						logger.debug("Stateful Service(storeInfoScheduler) - inputURIs:\n"+inp);


						// for every inputUrl we create a transferObject which represents a file
						// we collect all of them in a set of objects and after storing the transfer
						//we also store the transferObjectSet ... 
						TransferObject obj=null;
						for(String inputUri : inputURIs){
							obj=new TransferObject();
							try {
								String changed=inputUri;
								if(inputUri.endsWith("/"))changed=inputUri.substring(0, inputUri.length()-1);

								obj.setSrcURI(changed);

							} catch (Exception e) {
								e.printStackTrace();
							}
							obj.setTransferid(transferId);
							transferObjects.add(obj);	
						}
						logger.debug("Stateful Service(storeInfoScheduler) - inputURIs.length=" + inputURIs.length+
								" - transferObjects.size()="+transferObjects.size());
			}			
			else{
				logger.error("Stateful Service " +
						"- InputUrls variable is null:\n");
				return null;
			}


			t.setDestinationFolder(destinationFolder);
			t.setOverwrite(overwrite);
			t.setUnzipFile(unzipFile);

			// if storageType = LocalGHN, it means we don't have any storage node 
			// we use the local node (agent) as a storage node
			if(typeOfStorage.toString().compareTo(storageType.LocalGHN.toString())==0){
				t.setTypeOfStorage(storageType.LocalGHN.toString());
			}// but if storageType = StorageManager, we do have a DataStorage node (usually remote node)
			else if(typeOfStorage.toString().compareTo(storageType.StorageManager.toString())==0){
				t.setTypeOfStorage(storageType.StorageManager.toString());
				if(schedulerObj.getSmDetails()!=null){
					String dataStorageId=null;
					boolean flagExists=false;
					//retrieving the data storage by checking first if it exists 
					Extent<?> resultExtent = this.dbManager.getPersistenceManager().getExtent(DataStorage.class, true);
					Iterator<?> iter = resultExtent.iterator();

					while (iter.hasNext()){
						DataStorage obj=(DataStorage)iter.next();
						if(obj.getType().compareTo(storageType.StorageManager.toString())==0){
							if(obj.getServiceName().compareTo(schedulerObj.getSmDetails().getServiceName())==0 &&
									obj.getAccessType().compareTo(schedulerObj.getSmDetails().getAccessType().toString())==0 &&
									obj.getOwner().compareTo(schedulerObj.getSmDetails().getOwner())==0 &&
									obj.getServiceClass().compareTo(schedulerObj.getSmDetails().getServiceClass())==0){
								flagExists=true;
								dataStorageId=obj.getDataStorageId();
								break;
							}							
						}
					}
					if(flagExists==false){
						//we create it 
						DataStorage datastorage=new DataStorage();
						datastorage.setDataStorageId(transferId.concat("-datastorageSM"));
						datastorage.setType(storageType.StorageManager.toString());	
						datastorage.setServiceClass(schedulerObj.getSmDetails().getServiceClass());
						datastorage.setServiceName(schedulerObj.getSmDetails().getServiceName());
						datastorage.setAccessType(schedulerObj.getSmDetails().getAccessType().toString());
						datastorage.setOwner(schedulerObj.getSmDetails().getOwner());
						dataStorageId=transferId.concat("-datastorageSM");
						try	{ // *** store the DataStorage in DB ***
							ServiceContext.getContext().getDbManager().storeStorage(datastorage);
						}
						catch(Exception e){
							logger.error("Stateful Service " +
									"- Exception in storing the DataStorage:\n");
							e.printStackTrace();
							return null;
						}
					}
					//set dataStorage
					t.setStorageId(dataStorageId);
				}
				else{
					logger.error("Stateful Service " +
							"- The Storage Type is 'Storage Manager' and there" +
							"is no smDetails \n");
					return null;
				}
			}
			else if(typeOfStorage.toString().compareTo(storageType.DataStorage.toString())==0){
				t.setTypeOfStorage(storageType.DataStorage.toString());
				if(schedulerObj.getOutputUrls()!=null){
					if(transferObjects.size()!=schedulerObj.getOutputUrls().length){
						logger.error("Stateful Service(storeStorage) " +
								"- The Storage Type is 'DataStorage' and the size" +
								"of inputUrls does not match with the size of outputUrls\n");
						return null;
					}
					int i=0;
					for(TransferObject obj : transferObjects){
						obj.setDestUri(schedulerObj.getOutputUrls()[i]);
						i++;
					}	
				}
				else{
					logger.error("Stateful Service(storeStorage) " +
							"- The Storage Type is 'DataStorage' and there" +
							"are no outputUrls in schedulerObj \n");
					return null;
				}		
			}
		}// **** If TreeBasedTransfer **** 
		else if (transferType.compareTo("TreeBasedTransfer")==0){
			t.setSourceId(schedulerObj.getTreeSourceID());
			t.setStorageId(schedulerObj.getTreeStorageID());
			XStream xstreamForPattern = new XStream();
			String patternString = 	xstreamForPattern.toXML(schedulerObj.getPattern());
			t.setPattern(patternString);
		}

		// *** store the transfer in DB *** in Parallel
		StoreTransferThread storeTransferThread = new StoreTransferThread(t);
		storeTransferThread.start();

		if(inputURIs!=null && transferObjects!=null){ //if we had inputUris and not DataSource identity
			// *** store the transfer objects in DB *** in Parallel
			StoreTransferObjectThread storeTransferObjectThread = new StoreTransferObjectThread(transferObjects, transferId);
			storeTransferObjectThread.start();
		}
		//we return the transferId of the Transfer in SchedulerDB
		return transferId;
	}

	/*
	 * cancelScheduledTransfer
	 * input: String CancelTransferMessage (transferId (the one in the schedulerDB) & isForceCancel)
	 * return: String with CallingSchedulerResult obj (xml)
	 * if exception or error occurred, CallingSchedulerResult contains the specific error message
	 */
	public String cancelScheduledTransfer(String msg) throws GCUBEFault {

		this.dbManager=ServiceContext.getContext().getDbManager();
		this.isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();

		List<String> errors = new ArrayList<String>();
		CallingSchedulerResult callingSchedulerResult = new CallingSchedulerResult();

		String tmpMsg=msg;
		tmpMsg.replaceAll("&lt;", "<");
		tmpMsg=tmpMsg.replaceAll("&gt;", ">");

		XStream xstream = new XStream();
		InfoCancelSchedulerMessage cancelObj= new InfoCancelSchedulerMessage();
		cancelObj=(InfoCancelSchedulerMessage)xstream.fromXML(tmpMsg);

		String transferId = cancelObj.getCancelTransferMessage().getTransferId();
		Boolean isForceCancel = cancelObj.getCancelTransferMessage().isForceStop();
		//logger.debug("Stateful Service(cancelScheduledTransfer) - transferId="+transferId);

		Transfer transfer = this.dbManager.getPersistenceManager().getObjectById(Transfer.class,transferId);
		String typeOfScheduleId = transfer.getTypeOfScheduleId();
		TypeOfSchedule typeOfSchedule = this.dbManager.getPersistenceManager().getObjectById(TypeOfSchedule.class, typeOfScheduleId);
		String agentId=transfer.getAgentId();
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
						flagExists=true;
						break;
					}
				}
				
				if(flagExists==false){
					String errorMsg="Stateful Service(cancelScheduledTransfer) -  Error - agent with hostname="+hostnameOfAgent+" does not exist in DB anymore";
					logger.debug(errorMsg);
					errors.add(errorMsg);
					callingSchedulerResult.setErrors(errors);
					String msgStr = callingSchedulerResult.toXML();
					return msgStr;
				}
			}
			else{
				String errorMsg="Stateful Service(cancelScheduledTransfer) -  Error - agent with hostname="+hostnameOfAgent+" does not exist in DB anymore";
				logger.debug(errorMsg);
				errors.add(errorMsg);
				callingSchedulerResult.setErrors(errors);
				String msgStr = callingSchedulerResult.toXML();
				return msgStr;
			}
		}
		
		try{
			//check for the status before we make the cancel
			String status = transfer.getStatus();
			logger.debug("Stateful Service(cancelScheduledTransfer) - status before Calling the cancelTransfer in Agent!! = "+status);
			if(status.compareTo("COMPLETED")==0 || status.compareTo("FAILED")==0){
				//if it's a periodically schedule we have to change its status to "CANCELED" 
				if(typeOfSchedule.getPeriodicallyScheduledId()!=null){
					try {
						this.dbManager.updateTransferStatus(transferId, "CANCELED");
						this.updateAgentStatistics("CANCELED", agent);
					} catch (Exception e) {
						logger.error("\nStateful Service(cancelScheduledTransfer) - " +
								"Exception in updating the status to Canceled\n");
						errors.add("Stateful Service(cancelScheduledTransfer) - " +
								"Exception in updating the status to Canceled\n"+e.getMessage());
						callingSchedulerResult.setErrors(errors);
						String msgStr = callingSchedulerResult.toXML();
						e.printStackTrace();
						return msgStr;	
					}
					callingSchedulerResult.setCancelResult("DONE");
					String msgStr = callingSchedulerResult.toXML();
					return msgStr;	
				}//in other case there is no point to change it because this transfer is already done... 
				else{
					callingSchedulerResult.setCancelResult("Transfer already done");
					String msgStr = callingSchedulerResult.toXML();
					return msgStr;
				}
			}
			else if (status.compareTo("CANCELED")==0){
				callingSchedulerResult.setCancelResult("CANCELED");
				String msgStr = callingSchedulerResult.toXML();
				return msgStr;	
			}
			else if(status.compareTo("STANDBY")==0){
				//we change the status of transfer to CANCELED
				try {
					this.dbManager.updateTransferStatus(transferId, "CANCELED");
					this.updateAgentStatistics("CANCELED", agent);
				} catch (Exception e) {
					logger.error("\nStateful Service(cancelScheduledTransfer) - " +
							"Exception in updating the status to Canceled\n");
					errors.add("Stateful Service(cancelScheduledTransfer) - " +
							"Exception in updating the status to Canceled\n"+e.getMessage());
					callingSchedulerResult.setErrors(errors);
					String msgStr = callingSchedulerResult.toXML();
					e.printStackTrace();
					return msgStr;		
				}
				callingSchedulerResult.setCancelResult("DONE");
				String msgStr = callingSchedulerResult.toXML();
				return msgStr;
			}
			//else status "ONGOING"

			if(agentId==null){
				logger.debug("Stateful Service(cancelScheduledTransfer) - Error - agentId=null");
				errors.add("Stateful Service(cancelScheduledTransfer) - Error - agentId=null");
				callingSchedulerResult.setErrors(errors);
				String msgStr = callingSchedulerResult.toXML();
				return msgStr;
			}
			if(agent==null){
				logger.debug("Stateful Service(cancelScheduledTransfer) - Error - agent with id="+agentId+" does not exist anymore in DB");
				errors.add("Stateful Service(cancelScheduledTransfer) - Error - agent with id="+agentId+" does not exist anymore in DB");
				callingSchedulerResult.setErrors(errors);
				String msgStr = callingSchedulerResult.toXML();
				return msgStr;
			}

			String hostAgent=agent.getHost();
			int portAgent = agent.getPort();
			String scope = transfer.getScope();

			AgentLibrary agentLibrary = null;
			ScopeProvider.instance.set(scope); 
			agentLibrary =  transferAgent().at(hostAgent, portAgent).build();

			//retrieving the transferId 
			String transferIdOfAgent=null;
			boolean messagingEnabled=ServiceContext.getContext().isMessagingEnabled();
			if(messagingEnabled){
				//if we use the messaging the transfer id is the same with the one that the Agent Service has
				transferIdOfAgent= transfer.getTransferId();
			}
			else{
				//else the transfer id in the agent service is different with the one that the scheduler keeps 
				transferIdOfAgent= transfer.getTransferIdOfAgent();
			}
			
			if(transferIdOfAgent==null){
				logger.debug("Stateful Service(cancelScheduledTransfer) - Error - transferIdOfAgent=null");
				errors.add("Stateful Service(cancelScheduledTransfer) - Error - transferIdOfAgent=null");
				callingSchedulerResult.setErrors(errors);
				String msgStr = callingSchedulerResult.toXML();
				return msgStr;
			}

			agentLibrary.cancelTransfer(transferIdOfAgent, isForceCancel);
		}
		catch (Exception e) {
			logger.error("\nStateful Service(cancelScheduledTransfer) - " +
					"Exception in calling the cancelTransfer\n");		   	
			errors.add("Stateful Service(cancelScheduledTransfer) - " +
					"Exception in calling the cancelTransfer\n"+e.getMessage());
			callingSchedulerResult.setErrors(errors);
			String msgStr = callingSchedulerResult.toXML();
			e.printStackTrace();
			return msgStr;		
		}

		//we change the status of transfer to CANCELED
		try {
			this.dbManager.updateTransferStatus(transferId, "CANCELED");
			this.updateAgentStatistics("CANCELED", agent);
		} catch (Exception e) {
			logger.error("\nStateful Service(cancelScheduledTransfer) - " +
					"Exception in updating the status to Canceled\n");
			errors.add("Stateful Service(cancelScheduledTransfer) - " +
					"Exception in updating the status to Canceled\n"+e.getMessage());
			callingSchedulerResult.setErrors(errors);
			String msgStr = callingSchedulerResult.toXML();
			e.printStackTrace();
			return msgStr;					
		}
		logger.debug("\nStateful Service(cancelScheduledTransfer) - status="+transfer.getStatus());

		callingSchedulerResult.setCancelResult("DONE");
		String msgStr = callingSchedulerResult.toXML();
		return msgStr;	    
	}

	/*
	 * monitorScheduledTransfer
	 * input: String with the transferId (the one in the schedulerDB)
	 * return: String with result of monitoring (status in SchedulerDB)
	 */
	public String monitorScheduledTransfer(String msg) throws GCUBEFault {

		this.dbManager=ServiceContext.getContext().getDbManager();
		this.isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();

		String transferId = msg;
		String status = this.dbManager.getPersistenceManager().getObjectById(Transfer.class,transferId).getStatus();
		return status;
	}


	/*
	 * getScheduledTransferOutcomes
	 * input: String with the transferId (the one in the schedulerDB)
	 * return: String with CallingSchedulerResult obj (xml)
	 * if exception or error occurred, CallingSchedulerResult contains the specific error message
	 */
	public String getScheduledTransferOutcomes(String msg) throws GCUBEFault {

		this.dbManager=ServiceContext.getContext().getDbManager();
		this.isManagerForAgents=ServiceContext.getContext().getIsManagerForAgents();

		List<String> errors = new ArrayList<String>();
		StringBuilder outcomes = new StringBuilder();
		CallingSchedulerResult callingSchedulerResult = new CallingSchedulerResult();

		String transferId = msg;
		Transfer transfer = this.dbManager.getPersistenceManager().getObjectById(Transfer.class,transferId);
		
		String messg="\n";
		if(transfer.getTreeOutcomes()==null)messg=messg+"transfer.getTreeOutcomes()==null"+"\n";
		else {
			messg=messg+"transfer.getTreeOutcomes().size()="+transfer.getTreeOutcomes().length+"\n";
			for(String id:transfer.getTreeOutcomes()){
				messg=messg+"id="+id+"\n";
			}
		}		
		if(transfer.getOutcomes()==null)messg=messg+"transfer.getOutcomes()==null"+"\n";
		else {
			messg=messg+"transfer.getOutcomes().size()="+transfer.getOutcomes().length+"\n";
			for(String id:transfer.getOutcomes()){
				messg=messg+"id="+id+"\n";
			}
		}
		
		logger.debug("TEMP PRINT ... ."+messg);

		
		if(transfer.getPattern()!=null && transfer.getSourceId()!=null && transfer.getStorageId()!=null){
			//tree based transfer
			String[] outcomeIds=transfer.getTreeOutcomes();
			if(outcomeIds==null){
				logger.debug("no treeoutcomes stored");
				errors.add("no treeoutcomes stored");
				callingSchedulerResult.setErrors(errors);
				String msgStr = callingSchedulerResult.toXML();
				return msgStr;
			}
			for(String outcomeId:outcomeIds){
				TransferTreeOutcome transferOutcome = this.dbManager.getPersistenceManager().getObjectById(TransferTreeOutcome.class,outcomeId);
				outcomes.append("Outcome-0\n");
				outcomes.append("SubmittedDateOfTransfer: "+transferOutcome.getSubmittedDateOfTransfer()+"\n");
				outcomes.append("Exception: "+transferOutcome.getTreeException()+"\n");
				outcomes.append("SourceID: "+ transferOutcome.getSourceId()+"\n");
				outcomes.append("DestID: "+ transferOutcome.getStorageId()+"\n");
				outcomes.append("ReadTrees: "+ transferOutcome.getTotalReadTrees()+"\n");
				outcomes.append("WrittenTrees: "+ transferOutcome.getTotalWrittenTrees()+"\n");
				outcomes.append("Success?: "+ transferOutcome.isSuccess()+"\n");
				outcomes.append("Failure?: "+ transferOutcome.isFailure()+"\n");
			}		
			
			callingSchedulerResult.setSchedulerOutcomes(outcomes.toString());
			String msgStr = callingSchedulerResult.toXML();
			return msgStr;
		}//file based
		else if(transfer.getOutcomes()!=null){
			String[] outcomeIds=transfer.getOutcomes();
			for(String outcomeId:outcomeIds){
				TransferOutcome transferOutcome = this.dbManager.getPersistenceManager().getObjectById(TransferOutcome.class,outcomeId);
				outcomes.append("Outcome-"+transferOutcome.getNumberOfOutcomeInThisTransfer()+"\n");
				outcomes.append("SubmittedDateOfTransfer: "+transferOutcome.getSubmittedDateOfTransfer()+"\n");
				outcomes.append("Exception: "+transferOutcome.getException()+"\n");
				outcomes.append("FileName: "+ transferOutcome.getFileName()+"\n");
				outcomes.append("Dest: "+ transferOutcome.getDest()+"\n");
				outcomes.append("TransferTime: "+ transferOutcome.getTransferTime()+"\n");
				outcomes.append("TransferredBytes: "+ transferOutcome.getTransferredBytesOfObj()+"\n");
				outcomes.append("Size: "+ transferOutcome.getSize()+"\n");
				outcomes.append("Success?: "+ transferOutcome.isSuccess()+"\n");
				outcomes.append("Failure?: "+ transferOutcome.isFailure()+"\n");
			}			
			
			callingSchedulerResult.setSchedulerOutcomes(outcomes.toString());
			String msgStr = callingSchedulerResult.toXML();
			return msgStr;
		}
		// else we have to contact agent service in order to retrieve them
		logger.debug("Stateful Service(getScheduledTransferOutcomes) - no outcomes are stored in the scheduler DB.. so we call agent service to retrive them");

		//retrieving the transferId 
		String transferIdOfAgent=null;
		boolean messagingEnabled=ServiceContext.getContext().isMessagingEnabled();
		if(messagingEnabled){
			//if we use the messaging the transfer id is the same with the one that the Agent Service has
			transferIdOfAgent= transfer.getTransferId();
		}
		else{
			//else the transfer id in the agent service is different with the one that the scheduler keeps 
			transferIdOfAgent= transfer.getTransferIdOfAgent();
		}
		
		if(transferIdOfAgent==null){
			callingSchedulerResult.setSchedulerOutcomes("The Transfer has not started yet or It is a sync op.");
			String msgStr = callingSchedulerResult.toXML();
			return msgStr;
		}

		//retrieving the agentId
		String agentId = transfer.getAgentId();
		if(agentId==null){
			logger.debug("Stateful Service(getScheduledTransferOutcomes) - Error - agentId=null");
			errors.add("Stateful Service(getScheduledTransferOutcomes) - Error - agentId=null");
			callingSchedulerResult.setErrors(errors);
			String msgStr = callingSchedulerResult.toXML();
			return msgStr;
		}

		Agent tmpAgent=null;
		//retrieving the Agent by checking first if it exists 
		Extent<?> resultExtent = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
		Iterator<?> iter = resultExtent.iterator();
		boolean flagExists=false;
		while (iter.hasNext()){
			Agent obj=(Agent)iter.next();
			if(obj.getAgentId().compareTo(agentId)==0){
				tmpAgent=obj;
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
				tmpAgent=null;
				//retrieving the Agent again...
				Extent<?> resultExtent2 = this.dbManager.getPersistenceManager().getExtent(Agent.class, true);
				Iterator<?> iter2 = resultExtent2.iterator();
				flagExists=false;
				while (iter2.hasNext()){
					Agent obj=(Agent)iter2.next();
					if(obj.getAgentId().compareTo(agentId)==0){
						tmpAgent=obj;
						flagExists=true;
						break;
					}
				}
				
				if(flagExists==false){
					String errorMsg="Stateful Service(getScheduledTransferOutcomes) -  Error - agent with hostname="+hostnameOfAgent+" does not exist in DB anymore";
					logger.debug(errorMsg);
					errors.add(errorMsg);
					callingSchedulerResult.setErrors(errors);
					String msgStr = callingSchedulerResult.toXML();
					return msgStr;
				}
			}
			else{
				String errorMsg="Stateful Service(getScheduledTransferOutcomes) -  Error - agent with hostname="+hostnameOfAgent+" does not exist in DB anymore";
				logger.debug(errorMsg);
				errors.add(errorMsg);
				callingSchedulerResult.setErrors(errors);
				String msgStr = callingSchedulerResult.toXML();
				return msgStr;
			}
		}
		
		
		String hostAgent= tmpAgent.getHost();
		int portAgent = tmpAgent.getPort();
		String scope = transfer.getScope();
		AgentLibrary agentLibrary = null;	

		try{
			ScopeProvider.instance.set(scope); 
			agentLibrary =  transferAgent().at(hostAgent, portAgent).build();
		}
		catch (Exception e) {
			logger.error("Stateful Service(getScheduledTransferOutcomes) - Exception when building agentLibrary");
			errors.add("Stateful Service(getScheduledTransferOutcomes) - Exception when building agentLibrary\n"+e.getMessage());
			callingSchedulerResult.setErrors(errors);
			String msgStr = callingSchedulerResult.toXML();
			e.printStackTrace();
			return msgStr;
		}

		//to do - we can get the ids of objects in scheduler that failed or succeeded. 
		//String[] objectTrasferredIDs = transfer.getObjectTrasferredIDs();
		//String[] objectFailedIDs= transfer.getObjectFailedIDs();

		//*** getTransferOutcomes *** //
		ArrayList<FileTransferOutcome> outcomesArray=null;
		try {
			outcomesArray = agentLibrary.getTransferOutcomes(transferIdOfAgent, FileTransferOutcome.class);			
		} catch (Exception e) {
			logger.error("Stateful Service(getScheduledTransferOutcomes) - Exception when calling agentLibrary.getTransferOutcomes(..)");
			errors.add("Stateful Service(getScheduledTransferOutcomes) - Exception when calling agentLibrary.getTransferOutcomes(..)\n"+e.getMessage());
			callingSchedulerResult.setErrors(errors);
			String msgStr = callingSchedulerResult.toXML();
			e.printStackTrace();
			return msgStr;
		}	
		List<String> listOfOutcomesToBeStored=null;
		long total_size=0;
		long transferredBytes=0;
		List<String> listOutcomes=new ArrayList<String>();
		if(outcomesArray!=null){
			try{
				int numOfObj=0;
				List<String> tmpOutcomes=new ArrayList<String>();
				for (FileTransferOutcome outcome : outcomesArray){
					TransferOutcome transferOutcome = new TransferOutcome();
					String outcomeId = uuidgen.nextUUID();
					transferOutcome.setTransferOutcomesId(outcomeId);
					transferOutcome.setTransferId(transferId);
					transferOutcome.setSubmittedDateOfTransfer(transfer.getSubmittedDate());
					transferOutcome.setFileName(outcome.getFilename());
					transferOutcome.setException(outcome.getException());
					transferOutcome.setFailure(outcome.isFailure());
					transferOutcome.setSuccess(outcome.isSuccess());
					transferOutcome.setDest(outcome.getDest());
					transferOutcome.setTransferTime(outcome.getTransferTime()+"");
					transferOutcome.setTransferredBytesOfObj(outcome.getTransferredBytes().toString());
					transferOutcome.setSize(outcome.getTotal_size().toString());
					transferOutcome.setNumberOfOutcomeInThisTransfer(numOfObj);

					if(outcome.getTotal_size()!=null)total_size=total_size + outcome.getTotal_size();
					if(outcome.getTransferredBytes()!=null)transferredBytes=transferredBytes + outcome.getTransferredBytes();


					this.dbManager.storeTransferOutcomes(transferOutcome);

					outcomes.append("Outcome-"+transferOutcome.getNumberOfOutcomeInThisTransfer()+"\n");
					outcomes.append("SubmittedDateOfTransfer: "+transferOutcome.getSubmittedDateOfTransfer()+"\n");
					outcomes.append("Exception: "+transferOutcome.getException()+"\n");
					outcomes.append("FileName: "+ transferOutcome.getFileName()+"\n");
					outcomes.append("Dest: "+ transferOutcome.getDest()+"\n");
					outcomes.append("TransferTime: "+ transferOutcome.getTransferTime()+"\n");
					outcomes.append("TransferredBytes: "+ transferOutcome.getTransferredBytesOfObj()+"\n");
					outcomes.append("Size: "+ transferOutcome.getSize()+"\n");
					outcomes.append("Success?: "+ transferOutcome.isSuccess()+"\n");
					outcomes.append("Failure?: "+ transferOutcome.isFailure()+"\n");

					tmpOutcomes.add(outcomeId);
					numOfObj++;
				}
				
				String[] arrayOutcomes=transfer.getOutcomes();
				if(arrayOutcomes!=null){
					for(String tmp:arrayOutcomes)listOutcomes.add(tmp);
				}
				listOutcomes.addAll(tmpOutcomes);
			} catch (Exception e) {
				logger.error("Stateful Service(getScheduledTransferOutcomes) - Exception when calling dbManager.storeTransferOutcomes");
				errors.add("Stateful Service(getScheduledTransferOutcomes) - Exception when calling dbManager.storeTransferOutcomes\n"+e.getMessage());
				callingSchedulerResult.setErrors(errors);
				String msgStr = callingSchedulerResult.toXML();
				e.printStackTrace();
				return msgStr;
			}	
		}
		else{
			logger.error("No outcomes in the Agent/Scheduler Services");
			errors.add("No outcomes in the Agent/Scheduler Services");
			callingSchedulerResult.setErrors(errors);
			String msgStr = callingSchedulerResult.toXML();
			return msgStr;
		}

		try{
			this.dbManager.updateOutcomesInTransfer(transferId, listOutcomes);
			
		} catch (Exception e) {
			logger.error("Stateful Service(getScheduledTransferOutcomes) - Exception when calling dbManager.updateOutcomesInTransfer");
			errors.add("Stateful Service(getScheduledTransferOutcomes) - Exception when calling dbManager.updateOutcomesInTransfer\n"+e.getMessage());
			callingSchedulerResult.setErrors(errors);
			String msgStr = callingSchedulerResult.toXML();
			e.printStackTrace();
			return msgStr;
		}		
		//updating transferred bytes
		try {
			dbManager.updateTransferBytes(transfer.getTransferId(), total_size, transferredBytes);
		} catch (Exception e) {
			logger.error("Scheduler - Exception when call dbManager.updateTransferBytes ");
			e.printStackTrace();
		}

		callingSchedulerResult.setSchedulerOutcomes(outcomes.toString());
		String msgStr = callingSchedulerResult.toXML();
		return msgStr;
	}

	public void updateAgentStatistics(String status, Agent agent){
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

	/*
	 * getThread
	 */
	static Thread getThread( final String name ) {
		if ( name == null )
			throw new NullPointerException( "Null name" );
		final Thread[] threads = getAllThreads( );
		for ( Thread thread : threads )
			if ( thread.getName( ).equals( name ) )
				return thread;
				return null;
	}
	/*
	 * getAllThreads
	 */
	static Thread[] getAllThreads( ) {
		final ThreadGroup root = getRootThreadGroup( );
		final ThreadMXBean thbean = ManagementFactory.getThreadMXBean( );
		int nAlloc = thbean.getThreadCount( );
		int n = 0;
		Thread[] threads;
		do {
			nAlloc *= 2;
			threads = new Thread[ nAlloc ];
			n = root.enumerate( threads, true );
		} while ( n == nAlloc );
		return java.util.Arrays.copyOf( threads, n );
	}

	static ThreadGroup rootThreadGroup = null;

	/*
	 * getRootThreadGroup
	 */
	static ThreadGroup getRootThreadGroup( ) {
		if ( rootThreadGroup != null )
			return rootThreadGroup;
		ThreadGroup tg = Thread.currentThread( ).getThreadGroup( );
		ThreadGroup ptg;
		while ( (ptg = tg.getParent( )) != null )
			tg = ptg;
		return tg;
	}
	/*
	 * frequencyInMS
	 * input: FrequencyType (the one from the stubs)
	 * return: Long with the given frequency in MS
	 */
	public long frequencyInMS (FrequencyType frequency){
		if(frequency.getValue().compareTo(FrequencyType.perMinute.getValue())==0){
			return 1000*60;
		}
		else if(frequency.getValue().compareTo(FrequencyType.perHour.getValue())==0){
			return 1000*60*60;
		}
		else if(frequency.getValue().compareTo(FrequencyType.perDay.getValue())==0){
			return 1000*60*60*24;
		}
		else if(frequency.getValue().compareTo(FrequencyType.perWeek.getValue())==0){
			return 1000*60*60*24*7;
		}
		else if(frequency.getValue().compareTo(FrequencyType.perMonth.getValue())==0){
			return 1000*60*60*24*7*30;
		}
		else if(frequency.getValue().compareTo(FrequencyType.perYear.getValue())==0){
			return 1000*60*60*24*7*30*12;
		}
		return 0;		
	}	

	/*
	 * getResource
	 * input: Nothing
	 * return: Resource (the one represented from the specific submitter)
	 * ResourceException if no resource was found in the current context
	 */
	private SchedulerResource getResource() throws ResourceException {
		return (SchedulerResource) SchedulerContext.getContext().getWSHome().find();
	}

	private Calendar setCalendarComp(Calendar transferCal){
		Calendar calendarComp=(Calendar) transferCal.clone();
		calendarComp.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
		calendarComp.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
		calendarComp.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		calendarComp.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		calendarComp.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
		return calendarComp;
	}

	private void printDates(Calendar calendarTmp){
		String computerDate="year:"+Calendar.getInstance().get(Calendar.YEAR)+", "+
				"month:"+Calendar.getInstance().get(Calendar.MONTH)+", "+
				"day:"+Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+" - "+
				Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+"."+
				Calendar.getInstance().get(Calendar.MINUTE)+".";

		String transferDate="year:"+calendarTmp.get(Calendar.YEAR)+", "+
				"month:"+calendarTmp.get(Calendar.MONTH)+", "+
				"day:"+calendarTmp.get(Calendar.DAY_OF_MONTH)+" - "+
				calendarTmp.get(Calendar.HOUR_OF_DAY)+"."+
				calendarTmp.get(Calendar.MINUTE)+".";

		//long transferDateInMS=calendarTmp.getTimeInMillis();
		String stringDate = Utils.getFormattedCalendarString(calendarTmp);

		logger.debug("\nStateful Service(storeInfoScheduler) - dates:\ncomputerDate="+computerDate+"\ntransferDate="+transferDate+"\n" +
				"getFormattedCalendarString(instance)="+stringDate+"\n");
	}
}
