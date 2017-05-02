package org.gcube.datatransfer.scheduler.impl.porttype;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.scheduler.db.model.Transfer;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;

public class StoreTransferThread extends Thread{
	Transfer t;
	GCUBELog logger = new GCUBELog(this);

	public StoreTransferThread(Transfer t){		
		this.t=t;
	}

	public void run() {
		try	{ 
			ServiceContext.getContext().getDbManager().storeTransfer(t);
		}
		catch(Exception e){
			logger.error("StoreTransferThread " +
					"- Exception in storing the Transfer:\n");
			e.printStackTrace();
		}
	}
}
