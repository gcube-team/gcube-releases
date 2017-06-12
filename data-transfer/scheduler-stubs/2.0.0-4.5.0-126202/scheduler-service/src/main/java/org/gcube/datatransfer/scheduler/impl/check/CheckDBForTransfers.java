package org.gcube.datatransfer.scheduler.impl.check;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.spi.TimeZoneNameProvider;

import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.ManuallyScheduled;
import org.gcube.datatransfer.scheduler.db.model.PeriodicallyScheduled;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.db.model.TypeOfSchedule;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;
import org.gcube.datatransfer.scheduler.impl.handler.TransferHandler;
import org.gcube.datatransfer.scheduler.impl.newhandler.ProduceTransfer;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerResource;
import org.gcube.datatransfer.scheduler.impl.utils.Utils;





public class CheckDBForTransfers {

	public DataTransferDBManager dbManager=null;
	public SchedulerResource resource=null;
	public long checkForTransfersIntervalMS;
	public List<Transfer> transfers=null;
	GCUBELog logger = new GCUBELog(CheckDBForTransfers.class);
	private boolean isMessagingEnabled;


	public CheckDBForTransfers(GCUBEWSResource ws, List<Transfer> transfers) {
		this.dbManager=ServiceContext.getContext().getDbManager();	
		this.resource=(SchedulerResource) ws;
		this.transfers=transfers;		
		this.isMessagingEnabled=ServiceContext.getContext().isMessagingEnabled();
	}

	public void check() {

		for(Transfer obj : this.transfers){

			// ****check for typeOfSchedule ****//
			TypeOfSchedule typeOfSchedule = null;
			String idTypeOfSchedule = null;
			try{
				idTypeOfSchedule=obj.getTypeOfScheduleId();
				typeOfSchedule = this.dbManager.getPersistenceManager().getObjectById(TypeOfSchedule.class,idTypeOfSchedule );
			}catch(Exception e){
				logger.error("CheckForTransfers -- Exception in retrieving the typeOfSchedule");
				e.printStackTrace();
			}	

			String periodicallyScheduledId = typeOfSchedule.getPeriodicallyScheduledId();
			String idManuallyScheduled = typeOfSchedule.getManuallyScheduledId();			

			// ## if Direct Transfer - no schedule
			if(typeOfSchedule.isDirectedScheduled()==true){
				logger.debug("\nCheckForTransfers - "+obj.getSubmitter()+" -- A transfer (Direct Transfer) is happening today  - transferId:"+((Transfer)obj).getTransferId()+" -- submitter:"+((Transfer)obj).getSubmitter()+" -- status="+((Transfer)obj).getStatus());

				try {	
					this.dbManager.updateTransferStatus(obj.getTransferId(), "ONGOING");
				}catch (Exception e) {
					logger.error("\nCheckForTransfers -- Exception in updating the transfer status - "+((Transfer)obj).getSubmitter() +" - id="+((Transfer)obj).getTransferId());
					e.printStackTrace();
				}

				if(isMessagingEnabled){
					// **** Transfer Handler With Messaging ****
					ProduceTransfer produceTransfer = new ProduceTransfer(obj.getTransferId(),this.resource,false);
					produceTransfer.start();
				}else{
					// **** Transfer Handler ****
					TransferHandler transferHandler = new TransferHandler(obj.getTransferId(),this.resource,false);
					transferHandler.start();
				}

			}// ## if Manual Transfer - a specific instance
			else if (idManuallyScheduled!=null){
				ManuallyScheduled manuallyScheduled  = this.dbManager.getPersistenceManager().getObjectById(ManuallyScheduled.class, idManuallyScheduled);
				Calendar calendarTmp = Utils.getCalendarBasedOnStringDate(manuallyScheduled.getCalendarString());					
				if(calendarTmp==null){
					//turn it failed
					logger.debug("\nCheckForTransfers - calendarTmp(from String Format) is null - we skip this transfer with id="+obj.getTransferId()+" and we turn its status to FAILED");
					// updating the transfer status
					try	{ 
						this.dbManager.updateTransferStatus(obj.getTransferId(),"FAILED");
					}
					catch(Exception e){
						logger.error("CheckDBForTransfers "+
								"- Exception in changing the transfer status\n");
						e.printStackTrace();
					}
					continue;
				}

				Calendar calendarComp=Utils.setCalendarComp(calendarTmp);

				long leftMillis = calendarTmp.getTimeInMillis()-calendarComp.getTimeInMillis();
				if(leftMillis<60000 ){ 
					logger.debug("\nCheckForTransfers - "+obj.getSubmitter()+" -- A transfer (Manually Scheduled) is happening today  - transferId:"+((Transfer)obj).getTransferId()+" -- submitter:"+((Transfer)obj).getSubmitter()+" -- status="+((Transfer)obj).getStatus());
					//if(leftMillis<0){
					try {	
						this.dbManager.updateTransferStatus(obj.getTransferId(), "ONGOING");
					}catch (Exception e) {
						logger.error("\nCheckForTransfers -- Exception in updating the transfer status - "+((Transfer)obj).getSubmitter() +" - id="+((Transfer)obj).getTransferId());
						e.printStackTrace();
					}
					//}		

					if(isMessagingEnabled){
						// **** Transfer Handler With Messaging ****
						ProduceTransfer produceTransfer = new ProduceTransfer(obj.getTransferId(),this.resource,false);
						produceTransfer.start();
					}else{
						// **** Transfer Handler ****
						TransferHandler transferHandler = new TransferHandler(obj.getTransferId(),this.resource,false);
						transferHandler.start();
					}
				}
			}// ## if Periodically Transfer - every minute/hour/day/.. etc
			else if(periodicallyScheduledId!=null){
				PeriodicallyScheduled periodicallyScheduled= this.dbManager.getPersistenceManager().getObjectById(PeriodicallyScheduled.class, periodicallyScheduledId );		
				Calendar startInstance= Utils.getCalendarBasedOnStringDate(periodicallyScheduled.getStartInstanceString());									
				
				if(startInstance==null){
					//turn it failed
					logger.debug("\nCheckForTransfers - startInstance(from String Format) is null - we skip this transfer with id="+obj.getTransferId()+" and we turn its status to FAILED");
					// updating the transfer status
					try	{ 
						this.dbManager.updateTransferStatus(obj.getTransferId(),"FAILED");
					}
					catch(Exception e){
						logger.error("CheckDBForTransfers "+
								"- Exception in changing the transfer status\n");
						e.printStackTrace();
					}
					continue;
				}


				Calendar calendarComp=Utils.setCalendarComp(startInstance);

				long leftMillis = startInstance.getTimeInMillis()-calendarComp.getTimeInMillis();
				if(leftMillis<60000 ){		
					//ADDED 7-6-13-------------------
					if(leftMillis<0){
						try{
							//dbManager.updateTransferStartInstance(obj.getTransferId(), calendarComp);
							dbManager.updateTransferStartInstanceString(obj.getTransferId(), Utils.getFormattedCalendarString(calendarComp));
						}catch (Exception e) {
							logger.error("\nCheckForTransfers -- Exception in updating the start instance (was old)- id="+((Transfer)obj).getTransferId());
							e.printStackTrace();
						}
					}
					//------------------------------
					logger.debug("\nCheckForTransfers - "+obj.getSubmitter()+" -- A transfer (Periodically Scheduled) is happening today  - transferId:"+((Transfer)obj).getTransferId()+" -- submitter:"+((Transfer)obj).getSubmitter()+" -- status="+((Transfer)obj).getStatus());
					try {							
						this.dbManager.updateTransferStatus(obj.getTransferId(), "ONGOING");
						//ADDED 11-6-13-------------------
						//reset progress in case of a 'periodical' transfer
						Transfer t = this.dbManager.getPersistenceManager().getObjectById(Transfer.class,obj.getTransferId() );
						if(t.getNum_updates()>0){
							logger.debug("\nCheckForTransfers(Periodically Scheduled) -- status="+((Transfer)obj).getStatus()+" .. reloading progress...");
							this.dbManager.resetProgressInTransfer(t.getTransferId());
						}
					}catch (Exception e) {
						logger.error("\nCheckForTransfers -- Exception in updating the transfer status - "+((Transfer)obj).getSubmitter() +" - id="+((Transfer)obj).getTransferId());
						e.printStackTrace();
					}

					if(isMessagingEnabled){
						// **** Transfer Handler With Messaging ****
						ProduceTransfer produceTransfer = new ProduceTransfer(obj.getTransferId(),this.resource,false);
						produceTransfer.start();
					}else{
						// **** Transfer Handler ****
						TransferHandler transferHandler = new TransferHandler(obj.getTransferId(),this.resource,false);
						transferHandler.start();
					}
				}
			}
		}

	}


}



