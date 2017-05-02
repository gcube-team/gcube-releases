package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LockTest {
	
	private String owner="rcirillo";
	private String localPath="src/test/resources/CostaRica.jpg";
	private String localPath2="src/test/resources/dog.jpg";
	private String localPathDownload="src/test/resources/download.png";
	private String remotePath="/test/img4/CostaRica1.jpg";
	private String absoluteLocalPath;
	private String absoluteLocalPath2;
	private String absoluteLocalPathDownload;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube/devsec";//"/CNR.it/ISTI";///gcube/devsec";
	private String serviceClass="JUnitTest-LockTest";
	private String serviceName="StorageManager";
	
	@Before
	public void getClient(){
		ScopeProvider.instance.set(scope);
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED).getClient();
			assertNotNull(client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLocalResources();
	//upload costaRica
		String id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		System.out.println("UploadByPath id: "+id);
		assertNotNull(id);
	}

	@Test
	public void lockTest(){
		//download & lock
		String idLock=client.lock().LFile(absoluteLocalPathDownload).RFile(remotePath);
		System.out.println("locked "+remotePath+" with id : "+idLock);
		try{
			client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		}catch(Exception  e){ 
			assertNotNull(e); 
		}
		//upload & unlock
		client.unlock(idLock).LFile(absoluteLocalPath2).RFile(remotePath);
		//download 
		client.get().LFile(absoluteLocalPathDownload).RFile(remotePath);
		// delete
		removeRemoteFile();
		
	}

	@After
	public void removeLocalFile(){
		File f=new File(newFilePath);
		f.delete();
		assertFalse(f.exists());
	}


	
	private void removeRemoteFile() throws RemoteBackendException{
		client.remove().RFile(remotePath);
		List<StorageObject> list=client.showDir().RDir("test/img4");
		assertTrue(list.isEmpty());
	}

	
	private void setLocalResources() {
		absoluteLocalPath=new File(localPath).getAbsolutePath();
		String dir=new File(absoluteLocalPath).getParent();
		newFilePath=dir+"/testJunit.jpg";
		absoluteLocalPath=new File(localPath).getAbsolutePath();
		absoluteLocalPath2=new File(localPath2).getAbsolutePath();
		absoluteLocalPathDownload=new File(localPathDownload).getAbsolutePath();
	}
	

}
