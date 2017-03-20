package org.gcube.datatransfer.scheduler.db.test;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.DataStorage;

public class TestDataStorage {

	/** The UUIDGen */
	private static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();
	static GCUBELog logger = new GCUBELog(TestDataStorage.class);

	private static DataTransferDBManager dbManager;
	public static void main(String[] args) {
		//creating a datastorage
				createDataStore();

	}
	public static void createDataStore(){
		String id=uuidgen.nextUUID();
		id=id+"-DataStore";
		
		dbManager=new DataTransferDBManager();

		//create object
		DataStorage dataStorage=new DataStorage();
		
		try{		
			logger.debug("TestDB - Setting DataStore Info");
			dataStorage.setType("RemoteNode");
			dataStorage.setDataStorageId(id);
			dataStorage.setDataStorageIdOfIS("test");
			String dataStorageLink="ftp://andrea:bilico1980@pcd4science3.cern.ch/testandrea";
			dataStorage.setDataStorageLink(dataStorageLink);
			logger.debug("TestDB - Storing DataStore Info"); 
			dbManager.storeStorage(dataStorage);
		}catch (Exception e){
			logger.error("TestDB - Exception in storing DataStore Info"); 
			e.printStackTrace();
		}
		System.out.println("datastorage id = "+id);
	}
}
