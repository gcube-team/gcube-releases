package org.gcube.datatransfer.agent.impl.event;

import java.util.List;

import javax.jdo.Query;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.jdo.Transfer;
import org.gcube.datatransfer.common.outcome.TransferStatus;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TransferDBChecker extends Thread{
	public GCUBELog logger = new GCUBELog(TransferDBChecker.class);
	private int intervalForDBCheckInMS;

	public TransferDBChecker(){	
		intervalForDBCheckInMS=ServiceContext.getContext().getIntervalForDBCheck();
		if(intervalForDBCheckInMS<1000 || intervalForDBCheckInMS>120000){
			logger.debug("TransferDBChecker  - replace value of intervalForDBCheckInMS to 20000ms because it was"+intervalForDBCheckInMS);
			intervalForDBCheckInMS=20000;
		}
	}

	public void run(){
		logger.debug("TransferDBChecker has been started ... (intervalForDBCheckInMS="+intervalForDBCheckInMS+")");
		while(true){
			//check db
			Query  query = ServiceContext.getContext().getDbManager().getPersistenceManager().newQuery(Transfer.class);
			List<Transfer> list = (List<Transfer>) query.execute();
			for(Transfer obj : list){
				//we retrieve only those whose status is valid , the last message has not 
				//been sent yet and they contain submitter endpoint(we do not want those that were performed without messaging)
				if(checkValidity(obj.getStatus()) &&
						!obj.isLastNotificationMsgSent() &&
						obj.getSubmitterEndpoint()!=null){
					//notify
					logger.debug("TransferDBChecker -  notifying ...");
					ProduceResponse.notify(obj.getId());
				}
			}
			//sleep
			specificTimeSleep();
		}
	}


	public void specificTimeSleep(){
		try {
			Thread.sleep(intervalForDBCheckInMS);
		} catch (InterruptedException e) {
			logger.error("\nTransferDBChecker (specificTimeSleep)-- InterruptedException-Unable to sleep");
			e.printStackTrace();
		}
	}

	public boolean checkValidity(String status){
		if(status==null)return false;
		else if(status.compareTo(TransferStatus.QUEUED.toString())==0 ||
				status.compareTo(TransferStatus.STARTED.toString())==0 ||
				status.compareTo(TransferStatus.DONE.toString())==0 ||
				status.compareTo(TransferStatus.DONE_WITH_ERRORS.toString())==0 ||
				status.compareTo(TransferStatus.CANCEL.toString())==0 ||
				status.compareTo(TransferStatus.FAILED.toString())==0){
			return true;
		}
		else return false;
	}
}
