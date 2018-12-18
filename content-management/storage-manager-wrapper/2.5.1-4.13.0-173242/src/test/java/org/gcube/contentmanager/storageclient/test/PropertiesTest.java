package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
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

public class PropertiesTest {

	private String owner="rcirillo";
	private String localPath="src/test/resources/CostaRica1.jpg";
	private String remotePath="/test/mimeTest/CostaRica1.jpg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube/devsec"; //"/d4science.research-infrastructures.eu"; ///gcube/devsec/devVRE"; //"/CNR.it/ISTI";//"/gcube/devsec/devVRE"; // /d4science.research-infrastructures.eu"; //"/d4science.research-infrastructures.eu"; //"/CNR.it/ISTI";//
	private String serviceClass="JUnitTest";
	private String serviceName="StorageManager";
	private String id;
	private String field="mimetype";
	private String value="image/png";
	
	@Before
	public void getClient() throws RemoteBackendException{
		ScopeProvider.instance.set(Costants.DEFAULT_SCOPE_STRING);
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.PUBLIC, Costants.DEFAULT_MEMORY_TYPE).getClient();
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
	public void getPropertyByPath() throws RemoteBackendException {
		String valueFound= client.getMetaInfo(field).RFile(remotePath);
		System.out.println("value found for property: "+field+" is "+valueFound);
		MyFile f= client.getMetaFile().RFile(remotePath);
		assertNotNull(f);
		print(f);
		String result= client.setMetaInfo(field, value).RFile(remotePath);
		System.out.println("new property set ");
		MyFile f1= client.getMetaFile().RFile(remotePath);
		print(f1);
		assertNotNull(f1);
		assertEquals(f1.getMimeType(), value);
		
		
	}
	
	@Test
	public void getPropertyById() throws RemoteBackendException {
		String valueFound= client.getMetaInfo(field).RFile(id);
		System.out.println("value found for property: "+field+" is "+valueFound);
		MyFile f= client.getMetaFile().RFile(id);
		assertNotNull(f);
		print(f);
		String result= client.setMetaInfo(field, value).RFile(id);
		System.out.println("new property set ");
		MyFile f1= client.getMetaFile().RFile(id);
		print(f1);
		assertNotNull(f1);
		assertEquals(f1.getMimeType(), value);
		
	}
	
//	@Test
	public void setProperty() throws RemoteBackendException {
		String result= client.setMetaInfo(field, value).RFile(id);
		MyFile f= client.getMetaFile().RFile(remotePath);
		assertNotNull(f);
		print(f);

	}
	
	@After
	public void removeRemoteFile() throws RemoteBackendException{
		String id=client.remove().RFile(remotePath);
		List<StorageObject> list=client.showDir().RDir("test/mimeTest");
		assertTrue(list.isEmpty());
		removeLocalFile();
	}
	
	private void print(MyFile f) {
		System.out.println("\t name "+f.getName());
		System.out.println("\t size "+f.getSize());
		System.out.println("\t owner "+f.getOwner());
		System.out.println("\t id "+f.getId());
		System.out.println("\t absolute remote path "+f.getAbsoluteRemotePath());
		System.out.println("\t remote path: "+f.getRemotePath());
		System.out.println("\t mimetype: "+f.getMimeType());
		System.out.println("\t lastOperation: "+f.getOperation());
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
