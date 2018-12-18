package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.test.utils.Costants;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.Before;
import org.junit.Test;

public class MoveTest {

	private String owner="rcirillo";
	private String localPath="src/test/resources/CostaRica1.jpg";
	private String remoteOriginalFilePath="/test/new/original.jpg";
	private String remoteMovePath="/moveDirFailTest/link.jpg";
	private String remoteMovePath2="/moveDirCopy/link.jpg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources/test";
	private IClient client;
//	private String scope="/gcube/devsec"; //"/d4science.research-infrastructures.eu"; //"/d4science.research-infrastructures.eu"; ////"/CNR.it/ISTI";//"/gcube"; 
	private String serviceClass="JUnitTest-Move";
	private String serviceName="StorageManager";
	
	@Before
	public void getClient() throws RemoteBackendException{
		ScopeProvider.instance.set(Costants.DEFAULT_SCOPE_STRING);
		
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, Costants.DEFAULT_MEMORY_TYPE).getClient();
			assertNotNull(client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLocalResources();
		String id=client.put(true).LFile(absoluteLocalPath).RFile(remoteOriginalFilePath);
		assertNotNull(id);
	}
	
	
	@Test
	public void moveTest() throws RemoteBackendException {
//first moved operation	
//		client.copyFile().from(remoteOriginalFilePath).to(remoteMovePath);
		client.moveFile().from(remoteOriginalFilePath).to(remoteMovePath);
//		client.moveFile().from("/test/img/").to(remoteMovePath);
		client.get().LFile(newFilePath).RFile(remoteMovePath);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		checkOriginalFileIsAlive();
		removeLocalFile();
 //second file moved
		client.moveFile().from(remoteMovePath).to(remoteMovePath2);
		client.get().LFile(newFilePath).RFile(remoteMovePath2);
		f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		checkMoveFileIsAlive(remoteMovePath2);
		removeLocalFile();
		removeRemoteFile();
	}


	private void setLocalResources() {
		absoluteLocalPath=new File(localPath).getAbsolutePath();
		String dir=new File(absoluteLocalPath).getParent();
		newFilePath=dir+"/testJunitMoveOp.jpg";
		absoluteLocalPath=new File(localPath).getAbsolutePath();
	}

	private void removeLocalFile(){
		File f=new File(newFilePath);
		f.delete();
		assertFalse(f.exists());
	}

	private void checkOriginalFileIsAlive() {
		String id=null;
		try{
			id=client.get().LFile(newFilePath).RFile(remoteOriginalFilePath);
			System.out.println("id: "+id);
		}catch(Exception e ){}
		assertNull(id);
	}
	
	private void removeRemoteFile() {
		client.remove().RFile(remoteMovePath2);
		
		
	}

	private void checkMoveFileIsAlive(String remoteMovePath) {
		String id=client.get().LFile(newFilePath).RFile(remoteMovePath);
		System.out.println("id link is alive: "+id);
		assertNotNull(id);
		removeLocalFile();
	}



}
