package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetMetaFileTest {

	private String owner="rcirillo";
	private String localPath="src/test/resources/CostaRica1.jpg";
	private String remotePath="/test/img/CostaRica1.jpg";
	private String remotePath2="/test/img/CostaRica2.jpg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube/devsec"; //"/d4science.research-infrastructures.eu"; ///gcube/devsec/devVRE"; //"/CNR.it/ISTI";//"/gcube/devsec/devVRE"; // /d4science.research-infrastructures.eu"; //"/d4science.research-infrastructures.eu"; //"/CNR.it/ISTI";//
	private String serviceClass="JUnitTest";
	private String serviceName="StorageManager";
	private String id;
	private String id2;
	
	@Before
	public void getClient() throws RemoteBackendException{
		ScopeProvider.instance.set(scope);
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.PUBLIC, MemoryType.VOLATILE).getClient();
			assertNotNull(client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLocalResources();
		id=client.put(true, "image/jpeg").LFile(absoluteLocalPath).RFile(remotePath);
		id2=client.copyFile().from(remotePath).to(remotePath2);
		assertNotNull(id2);
		assertNotNull(id);
		
	}
	
	
	@Test
	public void getMetaFileByPath() throws RemoteBackendException {
		MyFile f= client.getMetaFile().RFile(remotePath);
//		System.out.println("mime is: "+f.getMimeType());
		client.put(true, "image/png").LFile("src/test/resources/dog.jpg").RFile(remotePath);
		f= client.getMetaFile().RFile(remotePath);
		 System.out.println("new mime is: "+f.getMimeType());
		assertNotNull(f);
		assertEquals(id, f.getId());
		print(f);
//		f= client.getMetaFile().RFile(remotePath2);
//		assertNotNull(f);
//		assertEquals(id2, f.getId());
//		print(f);
		
	}
	
	@Test
	public void getMetaFileById() throws RemoteBackendException {
		MyFile f= client.getMetaFile().RFile(id);
		assertNotNull(f);
		assertEquals(id, f.getId());
		print(f);
		f= client.getMetaFile().RFile(id2);
		assertNotNull(f);
		assertEquals(id2, f.getId());
		print(f);

	}
	
	@After
	public void removeRemoteFile() throws RemoteBackendException{
		String id=client.remove().RFile(remotePath);
		String id2=client.remove().RFile(remotePath2);
		List<StorageObject> list=client.showDir().RDir("test/img");
		assertTrue(list.isEmpty());
	}
	
	private void print(MyFile f) {
		System.out.println("\t name "+f.getName());
		System.out.println("\t size "+f.getSize());
		System.out.println("\t owner "+f.getOwner());
		System.out.println("\t id "+f.getId());
		System.out.println("\t absolute remote path "+f.getAbsoluteRemotePath());
		System.out.println("\t remote path: "+f.getRemotePath());
		System.out.println("\t mimetype: "+f.getMimeType());
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
