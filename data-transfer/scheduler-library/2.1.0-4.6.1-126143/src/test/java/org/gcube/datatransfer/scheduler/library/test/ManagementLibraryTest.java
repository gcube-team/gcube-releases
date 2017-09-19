package org.gcube.datatransfer.scheduler.library.test;


import static org.gcube.datatransfer.scheduler.library.proxies.Proxies.transferManagement;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.scheduler.library.ManagementLibrary;
import org.gcube.datatransfer.scheduler.library.outcome.CallingManagementResult;
import org.gcube.datatransfer.scheduler.library.outcome.TransferInfo;
import org.gcube.datatransfer.scheduler.library.outcome.TransferObjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagementLibraryTest {

	static Logger logger = LoggerFactory.getLogger("ManagementLibraryTest");
	public static void main (String [] args){
		ScopeProvider.instance.set("/gcube/devsec");

		//Management Library
		ManagementLibrary managementLibrary = null;		
		try {		
			//managementLibrary = (ManagementLibrary)transferSchedulerManagement().build();
			managementLibrary = (ManagementLibrary)transferManagement().at("node18.d.d4science.research-infrastructures.eu", 8081).withTimeout(10, TimeUnit.SECONDS).build();		 
			}catch (Exception e) {
			logger.error("exception when calling transferSchedulerManagement(..).at(..)\n"+e);
		}
		printAll(managementLibrary);
	}

	public static void printAll(ManagementLibrary managementLibrary){

		CallingManagementResult callingManagementResult= null;
		String resourceName="ALL";
		try{
			callingManagementResult=managementLibrary.getAllTransfersInfo(resourceName); 
		}catch (Exception e) {
			logger.error("exception when calling managementLibrary.getAllTransfersInfo(..).. \n"+e);
		}

		if(callingManagementResult==null)System.out.println("ManagementLibraryTest (printAll) - exception in calling the when calling endpoint.getAllTransfersInfo(message)");
		else{
			if(callingManagementResult.getAllTheTransfersInDB()!=null){
				System.out.println("\nManagementLibraryTest (printAll) - Retrieve Transfers by method (ResourceName="+resourceName+"):\n");
				for(TransferInfo obj: callingManagementResult.getAllTheTransfersInDB()){
					System.out.print("TransferId="+((TransferInfo)obj).getTransferId()+" -- Status="+((TransferInfo)obj).getStatus()+" -- Submitter="+((TransferInfo)obj).getSubmitter());
					if(((TransferInfo)obj).getTypeOfSchedule().isDirectedScheduled()){
						System.out.println(" -- directedScheduled");
					}
					else if(((TransferInfo)obj).getTypeOfSchedule().getManuallyScheduled()!=null){
						Calendar calendarTmp = ((TransferInfo)obj).getTypeOfSchedule().getManuallyScheduled().getCalendar();
						System.out.println(" -- manuallyScheduled for: "+calendarTmp.get(Calendar.DAY_OF_MONTH)+"-"+calendarTmp.get(Calendar.MONTH)+"-"+calendarTmp.get(Calendar.YEAR)+" (day-month-year) at "+calendarTmp.get(Calendar.HOUR_OF_DAY)+":"+calendarTmp.get(Calendar.MINUTE)+" (hour-minute)" );
					}
					else if(((TransferInfo)obj).getTypeOfSchedule().getPeriodicallyScheduled()!=null){
						System.out.println(" -- periodicallyScheduled with FrequencyType:" +((TransferInfo)obj).getTypeOfSchedule().getPeriodicallyScheduled().getFrequency().getValue());
					}
				}
			}
			System.out.println("");

			if(callingManagementResult.getAllTheTransferObjectsInDB()!=null){
				System.out.println("\nManagementLibraryTest (printAll) - Retrieve Transfer Objects by method (ResourceName="+resourceName+"):\n");
				for(TransferObjectInfo obj: callingManagementResult.getAllTheTransferObjectsInDB()){
					System.out.println("TransferObjId="+((TransferObjectInfo)obj).getObjectId()+" -- transferid="+((TransferObjectInfo)obj).getTransferid());
				}
			}
			System.out.println("");
		}
	}

}