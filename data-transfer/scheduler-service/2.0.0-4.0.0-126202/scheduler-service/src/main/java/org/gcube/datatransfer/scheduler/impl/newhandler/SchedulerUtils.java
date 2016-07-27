package org.gcube.datatransfer.scheduler.impl.newhandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.common.agent.Types.postProcessType;
import org.gcube.datatransfer.common.agent.Types.storageType;
import org.gcube.datatransfer.common.options.TransferOptions;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.Agent;
import org.gcube.datatransfer.scheduler.db.model.AgentStatistics;
import org.gcube.datatransfer.scheduler.db.model.PeriodicallyScheduled;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.db.model.TypeOfSchedule;
import org.gcube.datatransfer.scheduler.db.utils.Utils.FrequencyType;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerResource;
import org.gcube.datatransfer.scheduler.impl.utils.Utils;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class SchedulerUtils {

	private static DataTransferDBManager dbManager = ServiceContext.getContext().getDbManager();
	private static GCUBELog logger = new GCUBELog(SchedulerUtils.class);

		
	public static void updateStatusAndErrors(String transferId, Agent agent,String status, List<String> errorsInTransfer){
		// updating the transfer status
		try	{ 
			ServiceContext.getContext().getDbManager().updateTransferStatus(transferId,status);
		}
		catch(Exception e){
			logger.error("TransferHandler "+
					"- Exception in changing the transfer status\n");
			e.printStackTrace();
		}
		// updating the transfer errors if exist any
		if(errorsInTransfer!=null){
			try {					
				dbManager.updateTransferError(transferId, errorsInTransfer);
			}catch (Exception e) {
				logger.error("TransferHandler "+
						"- Exception in updating the transfer errors\n");
				e.printStackTrace();
			}
		}		

		if(status.compareTo("STANDBY")!=0&&status.compareTo("QUEUED")!=0){	
			//update the agent statistics
			updateAgentStatistics(status,agent);
		}
	}

	public static void updateAgentStatistics(String status, Agent agent){
		// updating the agent statistics
		if(agent==null)return;
		String agentIdOfIS=agent.getAgentIdOfIS();
		//retrieving the agentStatictics by checking first if exists
		AgentStatistics stats=null;		
		Extent<?> resultExtent = dbManager.getPersistenceManager().getExtent(AgentStatistics.class, true);
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
			dbManager.updateAgentStatistics(id, ongoing, failed, succeeded, canceled, total);
		} catch (Exception e) {
			logger.error("TransferHandler "+
					"- Exception in updating the agent Statistics\n");
			e.printStackTrace();
		}
	}
	
	//IT IS NOT USED ANYMORE
//	public static void addActiveTransferResource(SchedulerResource resource,String transferId){ 
//		String[] alreadyActiveTransferIDs = resource.getActiveTransfers();
//		String[] newActiveTransferIDS= new String[alreadyActiveTransferIDs.length+1];
//		int i=0;
//		for(String temp : alreadyActiveTransferIDs){
//			newActiveTransferIDS[i]=temp;
//			i++;
//		}
//		newActiveTransferIDS[i]=transferId;
//		try{
//			resource.setActiveTransfers(newActiveTransferIDS);
//			resource.store();
//		}catch (Exception e) {
//			logger.error("TransferHandler (addResourceOfActiveTransfer)-- Exception in Storing the Resource Property 'ActiveTransfers'"  );
//			e.printStackTrace();
//		}
//		try{
//			int alreadyNumberOfActive = Integer.valueOf(resource.getNumOfActiveTransfers());
//			String newNumberOfActive = (alreadyNumberOfActive+1)+"";
//			resource.setNumOfActiveTransfers(newNumberOfActive);
//			resource.store();
//		}catch (Exception e) {
//			logger.error("TransferHandler (addResourceOfActiveTransfer) -- Exception in Storing the Resource Property 'NumOfActiveTransfers'"  );
//			e.printStackTrace();
//		}
//	}
//
//	public static void removeActiveTransferResource(SchedulerResource resource,String transferId){
//		String[] alreadyActiveTransferIDs = resource.getActiveTransfers();
//		String[] newActiveTransferIDS= new String[alreadyActiveTransferIDs.length-1];
//		int i=0;
//		for(String temp : alreadyActiveTransferIDs){
//			if(transferId.compareTo(temp)==0)continue;
//			newActiveTransferIDS[i]=temp;
//			i++;
//		}
//		try{
//			resource.setActiveTransfers(newActiveTransferIDS);
//			resource.store();
//		}catch (Exception e) {
//			logger.error("TransferHandler (removeActiveTransferResource)-- Exception in Storing the Resource Property 'ActiveTransfers'"  );
//			e.printStackTrace();
//		}
//		try{
//			int alreadyNumberOfActive = Integer.valueOf(resource.getNumOfActiveTransfers());
//			String newNumberOfActive = (alreadyNumberOfActive-1)+"";
//			resource.setNumOfActiveTransfers(newNumberOfActive);
//			resource.store();
//		}catch (Exception e) {
//			logger.error("TransferHandler (removeActiveTransferResource) -- Exception in Storing the Resource Property 'NumOfActiveTransfers'"  );
//			e.printStackTrace();
//		}
//	}
	
	public static void refreshPeriodicallyScheduledTransfer(String transferId, String submitter, Agent agent){
		//------------only in case of a periodically scheduled---------------------------//
		Transfer transfer = dbManager.getPersistenceManager().getObjectById(Transfer.class,transferId);
		String idTypeOfSchedule=transfer.getTypeOfScheduleId();
		TypeOfSchedule typeOfSchedule = dbManager.getPersistenceManager().getObjectById(TypeOfSchedule.class,idTypeOfSchedule );
		String periodicallyScheduledId = typeOfSchedule.getPeriodicallyScheduledId();	
		String retrivedStatus= transfer.getStatus();


		// if it's periodically scheduled we change (refresh with a new value) the startInstance
		// and we make the status of the transfer STANDBY again
		// but if it's been changed in CANCELED in the meantime we keep it as it is...
		if((periodicallyScheduledId!=null)&&(retrivedStatus!="CANCELED")){
			PeriodicallyScheduled periodicallyScheduled = dbManager.getPersistenceManager().getObjectById(PeriodicallyScheduled.class,periodicallyScheduledId );
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
				logger.error("\nTransferHandler -- Exception in forward the startInstance - "+submitter +" - id="+transferId);
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

			//	dbManager.updateTransferStartInstance(transferId, tmpCalendar);
				dbManager.updateTransferStartInstanceString(transferId, Utils.getFormattedCalendarString(tmpCalendar));
			}catch (Exception e) {
				logger.error("\nCheckForTransfers -- Exception in updating the transfer startInstance (2)- "+submitter +" - id="+transferId);
				e.printStackTrace();
			}

			updateStatusAndErrors(transferId,agent, "STANDBY",null);
		}
	}

	public static org.gcube.datatransfer.common.agent.Types.TransferOptions fillTransferOptions(TransferOptions options){
		org.gcube.datatransfer.common.agent.Types.TransferOptions stubOptions = new
				org.gcube.datatransfer.common.agent.Types.TransferOptions();
		stubOptions.setOverwrite(options.isOverwriteFile());
		stubOptions.setStorageType(options.getType());
		stubOptions.setTransferTimeout(options.getTransferTimeout());
		int size = 0;
		if (options.isCovertFile()) size++;
		if (options.isUnzipFile()) size++;
		if (options.isDeleteOriginalFile()) size++;

		if (size!=0){
			int i=0;
			postProcessType [] type = new postProcessType[size];
			if (options.isUnzipFile()){
				type[i] = postProcessType.FileUnzip;
				i++;}
			if (options.isCovertFile()){
				type[i] = postProcessType.FileConversion;
				stubOptions.setConversionType(options.getConversionType().name());
				i++;
			}
			if (options.isDeleteOriginalFile()){
				type[i] = postProcessType.OriginalFileRemove;
			}
			stubOptions.setPostProcess(Arrays.asList(type));

		}

		if (options.getType().name().compareTo(storageType.StorageManager.name()) ==0)
			stubOptions.setStorageManagerDetails(options.getStorageManagerDetails());
		return stubOptions;
	}
	

	
}
