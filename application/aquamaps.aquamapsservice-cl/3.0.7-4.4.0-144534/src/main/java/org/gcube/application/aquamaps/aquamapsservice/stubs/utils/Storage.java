package org.gcube.application.aquamaps.aquamapsservice.stubs.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.gcube.application.aquamaps.aquamapsservice.client.Constants;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Storage {

	private static final Logger logger = LoggerFactory.getLogger(Storage.class);
	
	public static IClient getPersistentClient(String callerIdentifier){
		if(callerIdentifier==null)callerIdentifier=Constants.SERVICE_NAME;
		return new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, callerIdentifier, AccessType.SHARED, MemoryType.PERSISTENT).getClient();
	}
	
	public static IClient getClient(String callerIdentifier){		
		if(callerIdentifier==null)callerIdentifier=Constants.SERVICE_NAME;
		return new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, callerIdentifier, AccessType.SHARED, MemoryType.VOLATILE).getClient();
	}
	
	public static String storeFile(String toStorePath, boolean persistent,String callerIdentifier){
		return (persistent?getPersistentClient(callerIdentifier):getClient(callerIdentifier)).put(true).LFile(toStorePath).RFile(UUID.randomUUID().toString());
	}
	
	public static File getFileById(String id, boolean remove,String callerIdentifier) throws RemoteBackendException, IOException{
		File toReturn=File.createTempFile("Stg", ".temp");
		IClient cl=getClient(callerIdentifier);
		cl.get().LFile(toReturn.getAbsolutePath()).RFile(id);
		if(remove){
			try{
				cl.remove().RFile(id);
			}catch(Exception e){
				logger.warn("Unable to delete "+id,e);
			}
		}
		return toReturn;
	}
}
