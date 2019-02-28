/**
 * 
 */
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
import org.junit.Before;
import org.junit.Test;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class ReplaceFileTest {

	private String owner="rcirillo";
	private String localPath="src/test/resources/CostaRica.jpg";//"src/test/resources/empty.txt";//"src/test/resources/CostaRica1.jpg";
	private String localPath1="src/test/resources/dog.jpg";
	private String localPath2="src/test/resources/download.jpg";
	private String remotePath="/tests/CostaRica.jpg";//"/tests/img/CostaRica1.jpg";//"/tests/img/empty.txt";//
	private String absoluteLocalPath;
	private String absoluteLocalPath1;
	private String absoluteLocalPath2;
//	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube";//"/d4science.research-infrastructures.eu"; //"/d4science.research-infrastructures.eu"; //"/gcube/devsec";//"/d4science.research-infrastructures.eu"; //"/CNR.it";////"/gcube/devsec";//""/CNR.it/ISTI";//"/gcube";//"/gcube/devNext/NextNext";//
	private String serviceClass="JUnitTest";
	private String serviceName="StorageManager";
	private String id;
	
	@Before
	public void getClient(){
		ScopeProvider.instance.set(Costants.DEFAULT_SCOPE_STRING);
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, Costants.DEFAULT_MEMORY_TYPE).getClient();
			assertNotNull(client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		setLocalResources();
	}


	@Test
	public void replaceByPath() throws RemoteBackendException {
		String id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		System.out.println("UploadByPath id: "+id);
		client.get().LFile(absoluteLocalPath).RFile(id);
		File f=new File(absoluteLocalPath);
		assertTrue(f.exists());
		long size=f.length();
		String newId=client.put(true).LFile(absoluteLocalPath1).RFile(remotePath);
		System.out.println("UploadByPath again id: "+newId);
		System.out.println("download file test by id");
		assertEquals(id, newId);
		client.get().LFile(absoluteLocalPath2).RFile(newId);
		File f1= new File(absoluteLocalPath2);
		assertTrue(f1.exists());
		long size1=f1.length();
		assertNotNull(newId);
		assertNotEquals(size, size1);
		removeRemoteFile();
	}
	

	@Test
	public void replaceById1() throws RemoteBackendException {
		String id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		System.out.println("UploadByPath id: "+id);
		client.get().LFile(absoluteLocalPath).RFileById(id);
		File f=new File(absoluteLocalPath);
		assertTrue(f.exists());
		long size=f.length();
		String newId=client.put(true).LFile(absoluteLocalPath1).RFileById(id);
		System.out.println("UploadByPath again id: "+newId);
		System.out.println("download file test by id");
		assertEquals(id, newId);
		client.get().LFile(absoluteLocalPath2).RFile(newId);
		File f1= new File(absoluteLocalPath2);
		assertTrue(f1.exists());
		long size1=f1.length();
		assertNotNull(newId);
		assertNotEquals(size, size1);
		removeRemoteFile();
	}

//	
//	@Test
//	public void replaceById2() throws RemoteBackendException {
//		String id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
//		System.out.println("UploadByPath id: "+id);
//		client.get().LFile(absoluteLocalPath).RFile(id);
//		assertTrue(new File(absoluteLocalPath).exists());
//		String newId=client.put(true).LFile(absoluteLocalPath1).RFileById(id);
//		System.out.println("UploadByPath again id: "+newId);
//		System.out.println("download file test by id");
//		assertEquals(id, newId);
//		client.get().LFile(absoluteLocalPath2).RFile(newId);
//		assertTrue(new File(absoluteLocalPath2).exists());
//		assertNotNull(id);
//		removeRemoteFile();
//	}

	@Test
	public void notReplaceById() throws RemoteBackendException {
		String id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		System.out.println("UploadByPath id: "+id);
		client.get().LFile(absoluteLocalPath).RFileById(id);
		File f=new File(absoluteLocalPath);
		assertTrue(f.exists());
		long size=f.length();
		String newId=client.put(false).LFile(absoluteLocalPath1).RFileById(id);
		System.out.println("UploadByPath again id: "+newId);
		System.out.println("download file test by id");
		assertEquals(id, newId);
		client.get().LFile(absoluteLocalPath2).RFile(newId);
		File f1= new File(absoluteLocalPath2);
		assertTrue(f1.exists());
		long size1=f1.length();
		assertNotNull(newId);
		assertEquals(size, size1);
		removeRemoteFile();
	}

	
	private void removeRemoteFile() throws RemoteBackendException{
		client.removeDir().RDir("tests");
		List<StorageObject> list=client.showDir().RDir("tests");
		assertTrue(list.isEmpty());
	}


	private void setLocalResources() {
		absoluteLocalPath=new File(localPath).getAbsolutePath();
		absoluteLocalPath1=new File(localPath1).getAbsolutePath();
		absoluteLocalPath2=new File(localPath2).getAbsolutePath();
	}
	
}
