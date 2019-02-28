package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.test.utils.Costants;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RemoveTest {

	private String owner="roberto.cirillo";
	private String localPath="src/test/resources/CostaRica1.jpg";
	private String remotePath="/test/CostaRica1.jpg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube"; // /d4science.research-infrastructures.eu"; //"/CNR.it/ISTI";//
	private String serviceClass="JUnitTest";//"org.gcube.portlets.user";//
	private String serviceName="StorageManager";//"home-library";
	private String id;
	private String dirToRemove="";//"/Share/fe5ed634-94f1-48a3-be1c-230920399b57/";//"/Home/fabio.sinibaldi/Workspace/Thredds main catalog/";//"/Home/andrea.rossi/Workspace/";//"/Home/gianpaolo.coro/Workspace/Trash/";
	
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
		id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		assertNotNull(id);
		
	}

//	@Test
	public void removeDirTest(){
		client.removeDir().RDir(dirToRemove);
	}

	
//	@Test
	public void removeMultipleFileTest(){
		removeMultipleFile();
	}


	
	/**
	 * 
	 */
	private void removeMultipleFile() {
		client.remove().RFile("5aa16dfe02cadc50bff0eaf1");
//		client.remove().RFile(remotePath2);
//		client.remove().RFile(remotePath3);
//		client.remove().RFile(remotePath4);
//		client.remove().RFile(remotePath5);
//		client.remove().RFile(remotePath6);
//		client.remove().RFile(remotePath7);
//		client.remove().RFile(remotePath8);
//		client.remove().RFile(remotePath9);
//		client.remove().RFile(remotePath10);
		
	}

	@After
	public void checkRemoveFile() throws RemoteBackendException{
		List<StorageObject> list=client.showDir().RDir(dirToRemove);
		System.out.println("#files "+list.size());
		assertTrue(list.isEmpty());
	}

	private void removeLocalFile(){
		File f=new File(newFilePath);
		f.delete();
		assertFalse(f.exists());
	}


	private void setLocalResources() {
		absoluteLocalPath=new File(localPath).getAbsolutePath();
		String dir=new File(absoluteLocalPath).getParent();
		newFilePath=dir+"/testJunit.jpg";
		absoluteLocalPath=new File(localPath).getAbsolutePath();
	}


}
