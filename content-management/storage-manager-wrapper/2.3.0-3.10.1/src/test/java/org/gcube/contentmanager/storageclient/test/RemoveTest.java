package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
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
	private String remotePath2="/test/CostaRica2.jpg";
	private String remotePath3="/test/CostaRica3.jpg";
	private String remotePath4="/test/CostaRica4.jpg";
	private String remotePath5="/test/CostaRica5.jpg";
	private String remotePath6="/test/CostaRica6.jpg";
	private String remotePath7="/test/CostaRica7.jpg";
	private String remotePath8="/test/CostaRica8.jpg";
	private String remotePath9="/test/CostaRica9.jpg";
	private String remotePath10="/test/CostaRica10.jpg";
	
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube"; // /d4science.research-infrastructures.eu"; //"/CNR.it/ISTI";//
	private String serviceClass="org.gcube.portlets.user";//"JUnitTest";
	private String serviceName="test-home-library";//"StorageManager";
	private String id;
	
	@Before
	public void getClient() throws RemoteBackendException{
		ScopeProvider.instance.set(scope);
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, MemoryType.PERSISTENT).getClient();
			assertNotNull(client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLocalResources();
		id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		assertNotNull(id);
		
	}

	@Test
	public void removeTest(){
//		client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
//		client.put(true).LFile(absoluteLocalPath).RFile(remotePath2);
//		client.put(true).LFile(absoluteLocalPath).RFile(remotePath3);
//		client.put(true).LFile(absoluteLocalPath).RFile(remotePath4);
//		client.put(true).LFile(absoluteLocalPath).RFile(remotePath5);
//		client.put(true).LFile(absoluteLocalPath).RFile(remotePath6);
//		client.put(true).LFile(absoluteLocalPath).RFile(remotePath7);
//		client.put(true).LFile(absoluteLocalPath).RFile(remotePath8);
//		client.put(true).LFile(absoluteLocalPath).RFile(remotePath9);
//		client.put(true).LFile(absoluteLocalPath).RFile(remotePath10);
		client.removeDir().RDir("/Home/roberto.cirillo/Workspace/Trash/");
	}


	
	@After
	public void checkRemoveFile() throws RemoteBackendException{
		List<StorageObject> list=client.showDir().RDir("test/remove");
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
