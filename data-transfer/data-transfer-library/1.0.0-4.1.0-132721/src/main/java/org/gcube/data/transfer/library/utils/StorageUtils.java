package org.gcube.data.transfer.library.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StorageUtils {

	public static final IClient getClient(){
		return new StorageClient("data-transfer", "data-transfer-library", ScopeUtils.getCurrentCaller(), AccessType.SHARED, MemoryType.VOLATILE).getClient();
	}
	
	
	//return Id
	public static final String putOntoStorage(File source) throws RemoteBackendException, FileNotFoundException{
		IClient client=getClient();
		log.debug("Uploading local file "+source.getAbsolutePath());
		return client.put(true).LFile(new FileInputStream(source)).RFile(Utils.getUniqueString());		
	}
	
	public static final boolean checkStorageId(String id){
		return getClient().getHttpUrl().RFile(id)!=null;
	}
	
	public static final String getUrlById(String id){
		IClient client=getClient();
		log.debug("Id is "+id);
		return client.getHttpUrl().RFile(id);
	}
	
	public static final void removeById(String id){
		IClient client=getClient();
		client.remove().RFile(id);
	}
}
