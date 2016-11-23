package org.gcube.datatransfer.scheduler.impl.porttype;

import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.scheduler.db.model.TransferObject;
import org.gcube.datatransfer.scheduler.impl.context.ServiceContext;

public class StoreTransferObjectThread extends Thread{
	Set<TransferObject> objs;
	String transferId;
	GCUBELog logger = new GCUBELog(this);

	public StoreTransferObjectThread(Set<TransferObject> objs, String transferId){	
		this.objs=objs;
		this.transferId=transferId;
	}

	public void run() {
		//max times of calling db if get an error is '3' ... 
		int numOfTrying=0;
		while(process()==false && numOfTrying<3){
			numOfTrying++;
		};
	}
	
	//returns true if everything ok, in other case false
	public boolean process(){
		try	{
			ServiceContext.getContext().getDbManager().storeTransferObject(objs);
		}
		catch(Exception e){
			logger.error("StoreTransferObjectThread " +
					"- Exception in storing the Set of Transfer Objects:\n");
			e.printStackTrace();
			return false;
		}
		try {
			ServiceContext.getContext().getDbManager().updateTransferReadyObjects(transferId,true);
		} catch (Exception e) {
			logger.error("StoreTransferObjectThread " +
					"- Exception in updating the ready objs flag:\n");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
