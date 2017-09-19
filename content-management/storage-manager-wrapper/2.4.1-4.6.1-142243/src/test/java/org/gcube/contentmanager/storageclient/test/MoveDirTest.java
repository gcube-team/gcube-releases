package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MoveDirTest {

	private String owner="rcirillo";
	private String localPath="src/test/resources/CostaRica1.jpg";
	private String remoteOriginalFilePath="/test/move/img/original.jpg";
	private String remoteOriginalFilePath2="/test/move/img/original2.jpg";
	private String remoteMovePath="/test/move/";
	private String remoteMovePath2="/test/trash/";
	private String remoteMoveFilePath="/test/trash/move/img/original.jpg";
	private String remoteMoveFilePath2="/test/trash/move/img/original2.jpg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube/devsec";//"/CNR.it/ISTI";//"/gcube"; // "/d4science.research-infrastructures.eu/FARM/VTI";//
	private String serviceClass="JUnitTest-MoveDir";
	private String serviceName="StorageManager";
	
	@Before
	public void getClient() throws RemoteBackendException{
		ScopeProvider.instance.set(scope);
		
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, MemoryType.VOLATILE).getClient();
			assertNotNull(client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLocalResources();
		String id=client.put(true).LFile(absoluteLocalPath).RFile(remoteOriginalFilePath);
		assertNotNull(id);
		id=client.put(true).LFile(absoluteLocalPath).RFile(remoteOriginalFilePath2);
		assertNotNull(id);
		id=client.put(true).LFile(absoluteLocalPath).RFile(remoteOriginalFilePath2+"_1");
		assertNotNull(id);
		id=client.put(true).LFile(absoluteLocalPath).RFile(remoteOriginalFilePath2+"_2");
		assertNotNull(id);
		id=client.put(true).LFile(absoluteLocalPath).RFile(remoteOriginalFilePath2+"_3");
		assertNotNull(id);
		id=client.put(true).LFile(absoluteLocalPath).RFile(remoteOriginalFilePath2+"_4");
		assertNotNull(id);
	}
	
	
	@Test
	public void moveTest() throws RemoteBackendException {
		System.out.println("start move");
		long time=System.currentTimeMillis();
//first moved operation	
		client.moveDir().from(remoteMovePath).to(remoteMovePath2);
		long totalTime=System.currentTimeMillis()-time;
		System.out.println("total Time "+totalTime);
		checkOriginalFileIsAlive(remoteOriginalFilePath);
		checkOriginalFileIsAlive(remoteOriginalFilePath2);
		client.get().LFile(newFilePath).RFile(remoteMoveFilePath);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		client.get().LFile(newFilePath).RFile(remoteMoveFilePath2);
		f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
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

	private void checkOriginalFileIsAlive(String remoteOriginalFilePath) {
		String id=null;
		try{
			id=client.get().LFile(newFilePath).RFile(remoteOriginalFilePath);
		}catch(RemoteBackendException e ){}
		assertNull(id);
	}

	private void checkMoveFileIsAlive() {
		String id=client.get().LFile(newFilePath).RFile(remoteMovePath);
		System.out.println("id link is alive: "+id);
		assertNotNull(id);
		removeLocalFile();
	}

	@After
	public void removeRemoteDirs(){
		client.removeDir().RDir(remoteMovePath2);
	}

}
