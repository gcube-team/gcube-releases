package org.gcube.application.perform.service.engine.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageUtils {

	private static final Logger log= LoggerFactory.getLogger(StorageUtils.class);

	
	
	public static final IClient getClient(){
		return new StorageClient("data-transfer", "data-transfer-library", ScopeUtils.getCaller(), AccessType.SHARED, MemoryType.VOLATILE).getClient();
	}
	
	
	//return Id
	public static final String putOntoStorage(File source) throws RemoteBackendException, FileNotFoundException{
		IClient client=getClient();
		log.debug("Uploading local file "+source.getAbsolutePath());
		String id=client.put(true).LFile(new FileInputStream(source)).RFile(UUID.randomUUID().toString());
		log.debug("File uploaded. ID : "+id);
		String toReturn= client.getHttpUrl().RFile(id);
		log.debug("Created URL : "+toReturn);
		return toReturn;
	}
	
}
