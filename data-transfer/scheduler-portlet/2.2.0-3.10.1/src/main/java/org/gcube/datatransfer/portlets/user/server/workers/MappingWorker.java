package org.gcube.datatransfer.portlets.user.server.workers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gcube.data.trees.patterns.EdgePattern;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageAccessType;
import org.gcube.datatransfer.common.agent.Types.storageType;

import org.gcube.datatransfer.portlets.user.shared.obj.CallingManagementResult;
import org.gcube.datatransfer.portlets.user.shared.obj.CallingSchedulerResult;
import org.gcube.datatransfer.portlets.user.shared.obj.SchedulerObj;
import org.gcube.datatransfer.portlets.user.shared.obj.TransferInfo;
import org.gcube.datatransfer.portlets.user.shared.obj.TransferObjectInfo;



public class MappingWorker {


	public MappingWorker() {
	}

	/*
	 * mappedCallingSchedulerResult
	 */
	public CallingSchedulerResult mappedCallingSchedulerResult(org.gcube.datatransfer.scheduler.library.outcome.CallingSchedulerResult result){
		CallingSchedulerResult mappedResult = new CallingSchedulerResult();
		
		//if(result.getCancelResult()==null)mappedResult.setCancelResult("");
		//else mappedResult.setCancelResult(result.getCancelResult());
		if(result.getCancelResult()!=null)mappedResult.setCancelResult(result.getCancelResult());
		if(result.getErrors()!=null)mappedResult.setErrors(result.getErrors());
		if(result.getMonitorResult()!=null)mappedResult.setMonitorResult(result.getMonitorResult());
		if(result.getPrintResult()!=null)mappedResult.setPrintResult(result.getPrintResult());
		if(result.getSchedulerOutcomes()!=null)mappedResult.setSchedulerOutcomes(result.getSchedulerOutcomes());
		if(result.getStatus()!=null)mappedResult.setStatus(result.getStatus());
		if(result.getTransferid()!=null)mappedResult.setTransferid(result.getTransferid());
		return mappedResult;
	}

	/*
	 * mappedcallingManagementResult
	 */
	public CallingManagementResult mappedcallingManagementResult(org.gcube.datatransfer.scheduler.library.outcome.CallingManagementResult result){
		CallingManagementResult callingManagementResult = new CallingManagementResult();

		List<TransferInfo> allTheTransfers = new ArrayList<TransferInfo>();
		List<TransferObjectInfo> allTheTransferObjects = new ArrayList<TransferObjectInfo>();

		if(result==null){
			System.out.println("'MappingWorker' - mappedcallingManagementResult: PROBLEM->given parameter 'result' was null ... ");
			return null;
		}

		if(result.getAllTheTransfersInDB()!=null){
			for(org.gcube.datatransfer.scheduler.library.outcome.TransferInfo transferInfo : result.getAllTheTransfersInDB()){
				TransferInfo tmp = new TransferInfo();			
				if(transferInfo.getTypeOfSchedule().isDirectedScheduled()){
					tmp.setTypeOfScheduleString("Direct");
				}
				else if(transferInfo.getTypeOfSchedule().getManuallyScheduled()!=null){
					Calendar tmpCalendar = transferInfo.getTypeOfSchedule().getManuallyScheduled().getCalendar();
					String stringDate = transferInfo.getTypeOfSchedule().getManuallyScheduled().getInstanceString();
					if(stringDate!=null){
						tmp.setTypeOfScheduleString("Manually Scheduled for: "+stringDate);
					}
					else if(tmpCalendar!=null){
						stringDate = getFormattedCalendarString(tmpCalendar);
						tmp.setTypeOfScheduleString("Manually Scheduled for: "+stringDate);
					}
					else{
						tmpCalendar=Calendar.getInstance();
						stringDate = getFormattedCalendarString(tmpCalendar);
						tmp.setTypeOfScheduleString("Manually Scheduled for: "+stringDate);
					}	
				}
				else if(transferInfo.getTypeOfSchedule().getPeriodicallyScheduled()!=null){
					Calendar tmpCalendar = transferInfo.getTypeOfSchedule().getPeriodicallyScheduled().getStartInstance();
					String stringDate = transferInfo.getTypeOfSchedule().getPeriodicallyScheduled().getStartInstanceString();

					String freq="N_A";
					if(transferInfo.getTypeOfSchedule().getPeriodicallyScheduled().getFrequency()!=null)freq=transferInfo.getTypeOfSchedule().getPeriodicallyScheduled().getFrequency().getValue();
					
					if(stringDate!=null){
						tmp.setTypeOfScheduleString("Periodically Scheduled with frequency: "+freq+" - next transfer: "+stringDate);
					}
					else if(tmpCalendar!=null){
						stringDate = getFormattedCalendarString(tmpCalendar);
						tmp.setTypeOfScheduleString("Periodically Scheduled with frequency: "+freq+" - next transfer: "+stringDate);
					}
					else{
						tmpCalendar=Calendar.getInstance();
						stringDate = getFormattedCalendarString(tmpCalendar);
						tmp.setTypeOfScheduleString("Periodically Scheduled with frequency: "+freq+" - next transfer: "+stringDate);
					}
									
				}
				else tmp.setTypeOfScheduleString("");

				if(transferInfo.getTransferIdOfAgent()!=null)tmp.setTransferIdOfAgent(transferInfo.getTransferIdOfAgent());
				if(transferInfo.getTransferId()!=null)tmp.setTransferId(transferInfo.getTransferId());
				if(transferInfo.getTransferError()!=null)tmp.setTransferError(transferInfo.getTransferError());
				if(transferInfo.getSubmitter()!=null)tmp.setSubmitter(transferInfo.getSubmitter());
				if(transferInfo.getStatus()!=null)tmp.setStatus(transferInfo.getStatus());
				if(transferInfo.getObjectTrasferredIDs()!=null)tmp.setObjectTrasferredIDs(transferInfo.getObjectTrasferredIDs());
				if(transferInfo.getObjectFailedIDs()!=null)tmp.setObjectFailedIDs(transferInfo.getObjectFailedIDs());
				if(transferInfo.getSubmittedDate()!=null)tmp.setSubmittedDate(transferInfo.getSubmittedDate());

				if(transferInfo.getTotal_size()!=0)tmp.setTotal_size(transferInfo.getTotal_size());
				if(transferInfo.getBytes_have_been_transferred()!=0)tmp.setBytes_have_been_transferred(transferInfo.getBytes_have_been_transferred());
				//calculate Progress .... 
				double progr = tmp.calculateProgress();
				//System.out.println("'MappingWorker' - mappedcallingManagementResult: calculatedProgress="+progr+"\n"+
				//		"getTotal_size()="+transferInfo.getTotal_size()+" - getBytes_have_been_transferred="+transferInfo.getBytes_have_been_transferred());

				if(transferInfo.getSubmittedDate()!=null)tmp.setSubmittedDate(transferInfo.getSubmittedDate());

				if(transferInfo.getNumOdUpdates()!=0)tmp.setNumOfUpdates(transferInfo.getNumOdUpdates());
				
				allTheTransfers.add(tmp);
			}
		}
		else {
			System.out.println("'MappingWorker' - mappedcallingManagementResult: result.getAllTheTransfersInDB()==null !!");
		}

		if(result.getAllTheTransferObjectsInDB()!=null){
			for(org.gcube.datatransfer.scheduler.library.outcome.TransferObjectInfo transferObjectInfo : result.getAllTheTransferObjectsInDB()){
				TransferObjectInfo tmp = new TransferObjectInfo();

				if(transferObjectInfo.getObjectId()!=null)tmp.setObjectId(transferObjectInfo.getObjectId());
				if(transferObjectInfo.getSize()!=null)tmp.setSize(transferObjectInfo.getSize());
				if(transferObjectInfo.getTransferid()!=null)tmp.setTransferid(transferObjectInfo.getTransferid());
				if(transferObjectInfo.getURI()!=null)tmp.setURI(transferObjectInfo.getURI().getPath());

				allTheTransferObjects.add(tmp);
			}
		}
		else {
			System.out.println("'MappingWorker' - mappedcallingManagementResult: result.getAllTheTransferObjectsInDB()==null !!");
		}
		//allTransfers=callingManagementResult.getAllTheTransfersInDB();
		callingManagementResult.setAllTheTransfersInDB(allTheTransfers);
		callingManagementResult.setAllTheTransferObjectsInDB(allTheTransferObjects);
		if(result.getErrors()!=null)callingManagementResult.setErrors(result.getErrors());
		if(result.getGetAllTransfersInfoResult()!=null)callingManagementResult.setGetAllTransfersInfoResult(result.getGetAllTransfersInfoResult());

		return callingManagementResult;
	}

	/*
	 * mappedSchedulerObj
	 */
	public org.gcube.datatransfer.scheduler.library.obj.SchedulerObj mappedSchedulerObj(SchedulerObj obj){
		org.gcube.datatransfer.scheduler.library.obj.SchedulerObj mappedObj = new  org.gcube.datatransfer.scheduler.library.obj.SchedulerObj();
		//mapping all the strings and booleans
		if(obj.getAgentHostname().compareTo("")!=0)mappedObj.setAgentHostname(obj.getAgentHostname());
		else mappedObj.setAgentHostname(null);
		if(obj.getDataSourceId().compareTo("")!=0)mappedObj.setDataSourceId(obj.getDataSourceId());
		else mappedObj.setDataSourceId(null);
		if(obj.getDataStorageId().compareTo("")!=0)mappedObj.setDataStorageId(obj.getDataStorageId());
		else mappedObj.setDataStorageId(null);
		if(obj.getDestinationFolder().compareTo("")!=0)mappedObj.setDestinationFolder(obj.getDestinationFolder());
		else mappedObj.setDestinationFolder(null);
		if(obj.getScope().compareTo("")!=0)mappedObj.setScope(obj.getScope());
		else mappedObj.setScope(null);
		if(obj.getTypeOfTransfer().compareTo("")!=0)mappedObj.setTypeOfTransfer(obj.getTypeOfTransfer());
		else mappedObj.setTypeOfTransfer(null);
		if(obj.getSubmittedDate().compareTo("")!=0)mappedObj.setSubmittedDate(obj.getSubmittedDate());
		else mappedObj.setSubmittedDate(null);

		/*
		System.out.println("'MappingWorker' (mappedSchedulerObj) - size of inputUrls="+obj.getInputUrls().size());
		if(obj.getInputUrls().size()>0){
			for(String tmp: obj.getInputUrls()){
				System.out.println("'MappingWorker' (mappedSchedulerObj) - urls: "+tmp);
			}
		}
		System.out.println("");
		 */
		if(obj.getTypeOfTransfer().compareTo("TreeBasedTransfer")==0){
			Pattern pattern = Patterns.any();
			
			
			//Patterns.tree(patterns);
			
			mappedObj.setPattern(pattern);
			mappedObj.setTreeSourceID(obj.getDataSourceId());
			mappedObj.setTreeStorageID(obj.getDataStorageId());
		}
		if(obj.getInputUrls().size()>0){
			String[] inputArray = obj.getInputUrls().toArray(new String[obj.getInputUrls().size()]);
			mappedObj.setInputUrls(inputArray); //urls will be modified later
			mappedObj.setOutputUrls(inputArray.clone()); //urls will be modified later
		}
		else {
			mappedObj.setInputUrls(null);
			mappedObj.setOutputUrls(null);
		}
		mappedObj.setOverwrite(obj.getOverwrite());
		mappedObj.setUnzipFile(obj.getUnzipFile());
		mappedObj.setSyncOp(obj.getSyncOp());
		//mapping the storage type
		if(obj.getStorageType().compareTo("")!=0){
			mappedObj.setTypeOfStorage(storageType.valueOf(obj.getStorageType()));
		}
		else mappedObj.setTypeOfStorage(null);

		//mapping the Storage Manager Details
		if(obj.getAccessType().compareTo("")!=0){
			StorageManagerDetails smDetails = new StorageManagerDetails();

			smDetails.setAccessType(storageAccessType.valueOf(obj.getAccessType()));
			smDetails.setOwner(obj.getOwner());
			smDetails.setServiceClass(obj.getServiceClass());
			smDetails.setServiceName(obj.getServiceName());
			mappedObj.setSmDetails(smDetails);
		}
		else mappedObj.setSmDetails(null);

		//mapping the type of schedule
		org.gcube.datatransfer.scheduler.library.obj.TypeOfSchedule typeOfSchedule = new org.gcube.datatransfer.scheduler.library.obj.TypeOfSchedule();
		if(obj.getTypeOfSchedule().getDirectedScheduled()){
			typeOfSchedule.setDirectedScheduled(true);				
		}
		else if(obj.getTypeOfSchedule().getManuallyScheduled().getInstanceString().compareTo("")!=0){
			org.gcube.datatransfer.scheduler.library.obj.ManuallyScheduled manuallyScheduled = new org.gcube.datatransfer.scheduler.library.obj.ManuallyScheduled();

			String instanceString = obj.getTypeOfSchedule().getManuallyScheduled().getInstanceString();
			Calendar tmpCalendar = Calendar.getInstance();
			Date tmpDate = new Date();
			DateFormat formatter = new SimpleDateFormat("dd.MM.yy-HH.mm");
			if(instanceString.compareToIgnoreCase("")!=0){
				try {
					tmpDate = formatter.parse(instanceString);
				} catch (Exception e) {
					e.printStackTrace();
				}
				tmpCalendar.setTime(tmpDate);
			}			
			manuallyScheduled.setCalendar(tmpCalendar);			
			manuallyScheduled.setInstanceString(obj.getTypeOfSchedule().getManuallyScheduled().getInstanceString());
			typeOfSchedule.setManuallyScheduled(manuallyScheduled);
		}
		else if(obj.getTypeOfSchedule().getPeriodicallyScheduled().getStartInstanceString().compareTo("")!=0){
			org.gcube.datatransfer.scheduler.library.obj.PeriodicallyScheduled periodicallyScheduled = new org.gcube.datatransfer.scheduler.library.obj.PeriodicallyScheduled();
			org.gcube.datatransfer.common.scheduler.Types.FrequencyType frequency;
			frequency = org.gcube.datatransfer.common.scheduler.Types.FrequencyType.fromString(obj.getTypeOfSchedule().getPeriodicallyScheduled().getFrequency());
			periodicallyScheduled.setFrequency(frequency);

			String startInstanceString = obj.getTypeOfSchedule().getPeriodicallyScheduled().getStartInstanceString();
			Calendar tmpCalendar = Calendar.getInstance();
			Date tmpDate = new Date();
			DateFormat formatter = new SimpleDateFormat("dd.MM.yy-HH.mm");
			if(startInstanceString.compareToIgnoreCase("")!=0){
				try {
					tmpDate = formatter.parse(startInstanceString);
				} catch (Exception e) {
					e.printStackTrace();
				}
				tmpCalendar.setTime(tmpDate);
			}
			periodicallyScheduled.setStartInstance(tmpCalendar);			
			periodicallyScheduled.setStartInstanceString(obj.getTypeOfSchedule().getPeriodicallyScheduled().getStartInstanceString());
			typeOfSchedule.setPeriodicallyScheduled(periodicallyScheduled);
		}
		mappedObj.setTypeOfSchedule(typeOfSchedule);

		return mappedObj;
	}
	public String getFormattedCalendarString(Calendar instance){
		Calendar calendar=Calendar.getInstance();
		calendar.set(Calendar.YEAR, instance.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, instance.get(Calendar.MONTH));
		calendar.set(Calendar.DAY_OF_MONTH, instance.get(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, instance.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, instance.get(Calendar.MINUTE));

		//"dd.MM.yy-HH.mm"
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy-HH.mm");
		String date = sdf.format(calendar.getTime());
		return date;
	}
}
