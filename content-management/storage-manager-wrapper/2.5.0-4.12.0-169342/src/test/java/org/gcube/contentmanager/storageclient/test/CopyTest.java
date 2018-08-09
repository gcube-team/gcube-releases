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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CopyTest {

	
	private String owner="rcirillo";
	private String localFrog="src/test/resources/CostaRica1.jpg";
	private String localDog="src/test/resources/dog.jpg";
	private String remoteOriginalFilePath="/test/img/original2.jpg";
	private String remoteCopyPath="/test/copyDir/link.jpg";
	private String absoluteLocalFrog;
	private String absoluteLocalDog;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String serviceClass="CopyTest";
	private String serviceName="StorageManager";
	private String newFilePath2;
	private long frogSize;
	private long dogSize;

	
	
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
//		String id=client.put(true).LFile(absoluteLocalFrog).RFile(remoteOriginalFilePath);
//		System.out.println("id of the following path "+remoteOriginalFilePath+" is: "+id );
//		assertNotNull(id);
		
	}

	/**
	 * 
	 * @throws RemoteBackendException
	 */
	@Test
	public void removeCopiedFileTest() throws RemoteBackendException {
		// put orignal file
		String id=client.put(true).LFile(absoluteLocalFrog).RFile(remoteOriginalFilePath);
		System.out.println("id of the following path "+remoteOriginalFilePath+" is: "+id );
//first copy 
		client.copyFile().from(remoteOriginalFilePath).to(remoteCopyPath);
		client.get().LFile(newFilePath).RFile(remoteCopyPath);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		removeCopiedFile(remoteCopyPath, "test/copyDir/");
		checkOriginalFileIsAlive();
//remove original file		
		removeRemoteOriginalFile();
 	}
	
	@Test
	public void removeOriginalFileTest() throws RemoteBackendException {
		// put orignal file
		String id=client.put(true).LFile(absoluteLocalFrog).RFile(remoteOriginalFilePath);
		System.out.println("id of the following path "+remoteOriginalFilePath+" is: "+id );
//first copy 
		client.copyFile().from(remoteOriginalFilePath).to(remoteCopyPath);
		client.get().LFile(newFilePath).RFile(remoteCopyPath);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
// remove original file
		removeRemoteOriginalFile();
//		removeCopiedFile(remoteCopyPath, "test/copyDir/");
		checkFileIsAlive(remoteCopyPath);
//remove original file		
		client.remove().RFile(remoteCopyPath);
		removeCopiedFile(remoteCopyPath, "test/copyDir/");
 	}

	@Test
	public void destinationAlreadyPresentTest() throws RemoteBackendException {
	// put orignal file	
		String id=client.put(true).LFile(absoluteLocalFrog).RFile(remoteOriginalFilePath);
		System.out.println("id of the following path "+remoteOriginalFilePath+" is: "+id );
	// put dog in destination location
		String previousDestinationId=client.put(false).LFile(absoluteLocalDog).RFile(remoteCopyPath);
		System.out.println("id loaded on destination place: "+previousDestinationId);
//first copy (frog)
		client.copyFile(true).from(remoteOriginalFilePath).to(remoteCopyPath);
// get new id		
		String newDestinationId=client.get().LFile(newFilePath).RFile(remoteCopyPath);
		System.out.println("new destination id "+newDestinationId);
// check if the id is persisted		
		assertEquals(previousDestinationId, newDestinationId);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		assertEquals(frogSize, f.length());
		removeLocalFile(newFilePath);
		removeLocalFile(newFilePath2);
// remove original file
		removeRemoteOriginalFile();
//		removeCopiedFile(remoteCopyPath, "test/copyDir/");
		checkFileIsAlive(remoteCopyPath);
//remove original file		
		removeCopiedFile(remoteCopyPath, "test/copyDir/");
 	}

	/**
	 * Replace a file previously copied by softCopy operation. In this case the file hasn't a payload associated but only a link field
	 * @throws RemoteBackendException
	 */
	@Test
	public void replaceDestinationTest() throws RemoteBackendException {
	// put orignal frog file	
		String id=client.put(true).LFile(absoluteLocalFrog).RFile(remoteOriginalFilePath);
		System.out.println("id of the following path "+remoteOriginalFilePath+" is: "+id );
// copy (frog)
		client.copyFile(true).from(remoteOriginalFilePath).to(remoteCopyPath);
// get destination id		
		String newDestinationId=client.get().LFile(newFilePath).RFile(remoteCopyPath);
		System.out.println("destination id "+newDestinationId);
// replace destination file with dog
		String newId=client.put(true).LFile(absoluteLocalDog).RFile(remoteCopyPath);
// get new destinationid		
		String newDestinationId2=client.get().LFile(newFilePath2).RFile(remoteCopyPath);
		assertEquals(newId, newDestinationId2);
		System.out.println("new destination id "+newDestinationId2);
		assertEquals(newDestinationId, newDestinationId2);
		File f =new File(newFilePath2);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
	// check if the remote file is a dog	
		assertEquals(dogSize, f.length());
//		removeCopiedFile(remoteCopyPath, "test/copyDir/");
		checkFileIsAlive(remoteOriginalFilePath);
//remove original file
		removeRemoteOriginalFile();
		removeCopiedFile(remoteCopyPath, "test/copyDir/");
		removeLocalFile(newFilePath);
		removeLocalFile(newFilePath2);

 	}

	
	private void removeLocalFile(){
		File f=new File(newFilePath);
		f.delete();
		assertFalse(f.exists());
	}

	private void removeLocalFile(String newFilePath){
		File f=new File(newFilePath);
		f.delete();
		assertFalse(f.exists());
	}


	private void setLocalResources() {
		File f1=new File(localFrog);
		absoluteLocalFrog=f1.getAbsolutePath();
		frogSize=f1.length();
		File f2=new File(localDog);
		absoluteLocalDog=f2.getAbsolutePath();
		dogSize=f2.length();
		String dir=new File(absoluteLocalFrog).getParent();
		newFilePath=dir+"/testJunitLink.jpg";
		newFilePath2= dir+"/testJunitLink2.jpg";
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


	private void removeRemoteOriginalFile() {
		client.remove().RFile(remoteOriginalFilePath);
		List<StorageObject> linkList=client.showDir().RDir("test/img");
		assertTrue(linkList.isEmpty());
	}
	
//	@After
	private void removeCopiedFile(String link, String dir) {
		System.out.println("remove  file at: "+link);
		client.remove().RFile(link);
		System.out.println("show dir: "+dir);
		client.removeDir().RDir(dir);
		List<StorageObject> list=client.showDir().RDir(dir);
		assertTrue(list.isEmpty());
	}

}
