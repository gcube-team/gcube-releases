package org.gcube.data.analysis.statisticalmanager.stubs.storage;

import java.io.File;
import java.util.UUID;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;

public class RemoteStorage {

	private IClient storage;
	
	private static final String PACKAGE_NAME="org.gcube.data.analysis.statisticalmanager";
	private static final String SERVICE_NAME="StatisticalManager";

	
	public RemoteStorage(){
		this.storage = new StorageClient(PACKAGE_NAME,
				SERVICE_NAME, SERVICE_NAME, AccessType.SHARED).getClient();
	}
	
	
	public String storeFile(File f,boolean randomName){
		return storage.put(true).LFile(f.getAbsolutePath()).RFile(randomName?UUID.randomUUID().toString():f.getName());			
	}

	public String getUri(String fileId){
		return storage.getUrl().RFile(fileId);
	}
	
	public void downloadFile(String fileId,String localPath){
		storage.get().LFile(localPath).RFile(fileId);
	}
	
	public void deleteRemoteFile(String fileId){
		storage.remove().RFile(fileId);
	}
	
}
