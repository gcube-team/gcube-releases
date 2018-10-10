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
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class DuplicateTest {

	private String owner="rcirillo";
	private String localPath="src/test/resources/CostaRica1.jpg";
	private String remoteOriginalFilePath="/test/img/original2.jpg";
	private String remoteCopyPath="/test/duplicateDir/link.jpg";
//	private String remoteCopyPath2="/test/duplicateDirCopy/link.jpg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private IClient client;
//	private String scope="/gcube/devsec"; //"/d4science.research-infrastructures.eu/gCubeApps";//"/d4science.research-infrastructures.eu/FARM";// "/d4science.research-infrastructures.eu/FARM/VTI";//
	private String serviceClass="DuplicateTest";
	private String serviceName="StorageManager";
	private String originalId;

	
	
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
		originalId=client.put(true).LFile(absoluteLocalPath).RFile(remoteOriginalFilePath);
		System.out.println("original id "+ originalId);
		assertNotNull(originalId);
		
	}

	/**
	 * 
	 * @throws RemoteBackendException
	 */
	@Test
	public void duplicateFilePathTest() throws RemoteBackendException {
//first copy 
		String id=client.duplicateFile().RFile(remoteOriginalFilePath);
		System.out.println("new id: "+id);
		client.get().LFile(newFilePath).RFileById(id);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		removeDuplicatedFile(id);
		checkOriginalFileIsAlive();
//remove original file		
//		removeRemoteOriginalFile();
 	}
	
	
	@Test
	public void duplicateFileIdTest() throws RemoteBackendException {
//first copy 
		String id=client.duplicateFile().RFileById(originalId);
		System.out.println("new id: "+id);
		client.get().LFile(newFilePath).RFileById(id);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		removeDuplicatedFile(id);
		checkOriginalFileIsAlive();
//remove original file		
//		removeRemoteOriginalFile();
 	}
	
//	@Test
	public void removeOriginalFileTest() throws RemoteBackendException {
//first copy 
		client.copyFile().from(remoteOriginalFilePath).to(remoteCopyPath);
		client.get().LFile(newFilePath).RFile(remoteCopyPath);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
// remove original file
		removeRemoteOriginalFile();
		checkFileIsAlive(remoteCopyPath);
//remove original file		
		client.remove().RFile(remoteCopyPath);
 	}
	
	private void removeLocalFile(){
		File f=new File(newFilePath);
		f.delete();
		assertFalse(f.exists());
	}


	private void setLocalResources() {
		absoluteLocalPath=new File(localPath).getAbsolutePath();
		String dir=new File(absoluteLocalPath).getParent();
		newFilePath=dir+"/testJunitLink.jpg";
		absoluteLocalPath=new File(localPath).getAbsolutePath();
	}
	
	private void checkOriginalFileIsAlive() {
		String id;
		id=client.get().LFile(newFilePath).RFile(remoteOriginalFilePath);
		System.out.println("id orig is alive: "+id);
		assertNotNull(id);
		removeLocalFile();
	}
	
	private void checkFileIsAlive(String remotePath) {
		String id;
		id=client.get().LFile(newFilePath).RFile(remotePath);
		System.out.println("id orig is alive: "+id);
		assertNotNull(id);
		removeLocalFile();
	}

	@After
	public void removeRemoteOriginalFile() {
		client.remove().RFile(remoteOriginalFilePath);
//		client.removeDir().RDir("test/img");
		List<StorageObject> linkList=client.showDir().RDir("test/img");
		assertTrue(linkList.isEmpty());
	}
	
	private void removeDuplicatedFile(String link) {
		System.out.println("remove file at: "+link);
		client.remove().RFile(link);
	}


}
