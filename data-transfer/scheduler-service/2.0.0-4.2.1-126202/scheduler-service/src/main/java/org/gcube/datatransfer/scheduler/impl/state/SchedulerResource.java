package org.gcube.datatransfer.scheduler.impl.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.ResourceProperty;

public class SchedulerResource extends GCUBEWSResource{

	/**
	 * 
	 */
	
	private static final String NAME_RP_NAME = "Name";
	private static final String NAME_RP_ACTIVE_TRANSFERS = "ActiveTransfers";
	private static final String NAME_RP_NUM_OF_ACTIVE_TRANSFERS = "NumOfActiveTransfers";
	private static final String NAME_RP_CHECK_DB_THREAD = "CheckDBThread";
	GCUBELog logger = new GCUBELog(this);

	/** Client unique name. */
	String name;

	
     /**{@inheritDoc}*/
    public void initialise(Object... args) throws Exception {
		if (args == null || args.length!=1) throw new IllegalArgumentException();
		this.setName((String) args[0]);
		String[] activeTransferIDs={};		
		this.setActiveTransfers(activeTransferIDs);
		this.setNumOfActiveTransfers("0");
		this.setCheckDBThread("");
	}

    public synchronized void setName(String name) {
    	this.name=name;
    	ResourceProperty property = this.getResourcePropertySet().get(NAME_RP_NAME);
    	property.clear();
    	property.add(name);
    }
    public synchronized String getName() {
    	return (String) this.getResourcePropertySet().get(NAME_RP_NAME).get(0); 
    }


	public  void setActiveTransfers (String[] array) {
    	ResourceProperty property = this.getResourcePropertySet().get(NAME_RP_ACTIVE_TRANSFERS);
    	property.clear();
    	property.add(array);
	}

	public  String[] getActiveTransfers () {
		String[] activeTransferIDs={};
		try{
			/*
			ArrayList<String> list = new ArrayList<String>();		
			Iterator it =  this.getResourcePropertySet().get(NAME_RP_ACTIVE_TRANSFERS).iterator() ;
			while (it.hasNext()){
				list.add((String)it.next());
			}
			 */
			String[] tempArray = (String[])this.getResourcePropertySet().get(NAME_RP_ACTIVE_TRANSFERS).get(0);
			logger.debug("ScheduleResource (getActiveTransfers)-- tempArray.length="+tempArray.length);
			if(tempArray.length==0)return activeTransferIDs;
			//activeTransferIDs = list.toArray(new String[list.size()]);
			activeTransferIDs = tempArray;
			/*
			int i=0;
			for(String activeTransfer : list){
			activeTransferIDs[i]=activeTransfer;
			i++;			
			}
			*/
		}catch (ArrayStoreException e) {
			logger.error("ScheduleResource (getActiveTransfers)-- ArrayStoreException in Storing the Resource Property 'ActiveTransfers'");
			e.printStackTrace();
		}catch (Exception e) {
		logger.error("ScheduleResource (getActiveTransfers)-- Exception in Storing the Resource Property 'ActiveTransfers'"  );
		e.printStackTrace();
		}
		return activeTransferIDs;		
	}
	
	public void setNumOfActiveTransfers(String msg) {
    	ResourceProperty property = this.getResourcePropertySet().get(NAME_RP_NUM_OF_ACTIVE_TRANSFERS);
    	property.clear();
    	property.add(msg);
	}
	public String getNumOfActiveTransfers() {
    	return (String) this.getResourcePropertySet().get(NAME_RP_NUM_OF_ACTIVE_TRANSFERS).get(0);
	}
    
	public void setCheckDBThread(String msg) {
    	ResourceProperty property = this.getResourcePropertySet().get(NAME_RP_CHECK_DB_THREAD);
    	property.clear();
    	property.add(msg);
	}
	public String getCheckDBThread() {
    	return (String) this.getResourcePropertySet().get(NAME_RP_CHECK_DB_THREAD).get(0);
	}
	
	@Override
	protected String[] getPropertyNames() {
		return new String[]{NAME_RP_NAME, NAME_RP_ACTIVE_TRANSFERS,NAME_RP_NUM_OF_ACTIVE_TRANSFERS, NAME_RP_CHECK_DB_THREAD };
	}

    
}
