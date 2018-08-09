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
import org.junit.Test;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class SoftCopyTest {

	private String owner="rcirillo";
	private String localFrog="src/test/resources/CostaRica1.jpg";//"src/test/resources/testFile.txt";//"src/test/resources/CostaRica1.jpg";
	private String localDog="src/test/resources/dog.jpg";
	private String remoteOriginalFilePath="/test/frog.jpg";
	private String remoteCopyPath="/test/SoftCopy1/frog1.jpg";
	private String remoteCopyPath2="/test/SoftCopy2/frog2.jpg";
	private String remoteCopyPath3="/test/SoftCopy3/frog3.jpg";
	private String remoteCopyDir="/test/copyDir/";
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

	/**After a copy the file copied is deleted and a check is performed on the original file
	 * 
	 * @throws RemoteBackendException
	 */
	@Test
	public void removeCopiedFileTest() throws RemoteBackendException {
		// put orignal file
		String id=client.put(true).LFile(absoluteLocalFrog).RFile(remoteOriginalFilePath);
		System.out.println("id of the following path "+remoteOriginalFilePath+" is: "+id );
//first copy 
		client.softCopy().from(remoteOriginalFilePath).to(remoteCopyPath);
		client.get().LFile(newFilePath).RFile(remoteCopyPath);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		removeCopiedFile(remoteCopyPath, remoteCopyDir);
		checkOriginalFileIsAlive();
//remove original file		
		removeRemoteOriginalFile();
 	}
	
	
	/**After a copy the original file is deleted and a check is performed on the copied file
	 * 
	 * @throws RemoteBackendException
	 */
	@Test
	public void removeOriginalFileTest() throws RemoteBackendException {
		// put orignal file
		String id=client.put(true).LFile(absoluteLocalFrog).RFile(remoteOriginalFilePath);
		System.out.println("id of the following path "+remoteOriginalFilePath+" is: "+id );
//first copy 
		client.softCopy().from(remoteOriginalFilePath).to(remoteCopyPath);
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

	
	/**After a copy the original file is deleted and a check is performed on the copied file
	 * 
	 * @throws RemoteBackendException
	 */
	@Test
	public void copyByIdTest() throws RemoteBackendException {
		// put orignal file
		String id=client.put(true).LFile(absoluteLocalFrog).RFile(remoteOriginalFilePath);
		System.out.println("id of the original path "+remoteOriginalFilePath+" is: "+id );
		assertNotNull(id);
//first copy 
//		String id2=client.softCopy().from(remoteOriginalFilePath).to(null);
		String id2=client.softCopy().from(id).to(null);
		System.out.println("id of the copied file without path is: "+id2 );
		assertNotNull(id2);
		client.get().LFile(newFilePath).RFile(id2);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
// remove original file
		removeRemoteOriginalFile();
//		removeCopiedFile(remoteCopyPath, "test/copyDir/");
//		checkFileIsAlive(remoteCopyPath);
		checkFileIsAlive(id2);
//remove original file		
//		client.remove().RFile(remoteCopyPath);
//		client.remove().RFile(id2);
//		removeCopiedFile(remoteCopyPath, "test/copyDir/");
//		removeCopiedFile(remoteCopyPath, "test/copyDir/");
		removeCopiedFile(id2, "test/copyDir/");
 	}

	
	/**
	 * Copy operation when the destination file is already present. 
	 * In this test there is a check on the id of the destination file. It should be the same of the previous file located there
	 * @throws RemoteBackendException
	 */
	@Test
	public void destinationAlreadyPresentTest() throws RemoteBackendException {
	// put orignal file	
		String id=client.put(true).LFile(absoluteLocalFrog).RFile(remoteOriginalFilePath);
		System.out.println("id of the following path "+remoteOriginalFilePath+" is: "+id );
	// put dog in destination location
		String previousDestinationId=client.put(true).LFile(absoluteLocalDog).RFile(remoteCopyPath);
		System.out.println("id loaded on destination place: "+previousDestinationId);
//first copy (frog)
		client.softCopy(true).from(remoteOriginalFilePath).to(remoteCopyPath);
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
		client.softCopy(true).from(remoteOriginalFilePath).to(remoteCopyPath);
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

	@Test
	public void copyAnAlreadyCopiedFile() throws RemoteBackendException {
		// put orignal frog file	
		String id=client.put(true).LFile(absoluteLocalFrog).RFile(remoteOriginalFilePath);
		System.out.println("id of the following path "+remoteOriginalFilePath+" is: "+id );
// copy (frog)
		String idCopy=client.softCopy(true).from(remoteOriginalFilePath).to(remoteCopyPath);
		System.out.println("copied file id: "+idCopy);
// new copy on frog
		String idCopy2=client.softCopy(true).from(remoteCopyPath).to(remoteCopyPath2);
		System.out.println("second copy file id: "+idCopy2);
// new copy on frog
		String idCopy3=client.softCopy(true).from(remoteOriginalFilePath).to(remoteCopyPath3);
		System.out.println("second copy file id: "+idCopy2);		
// check files
		checkFileIsAlive(remoteOriginalFilePath);
		checkFileIsAlive(remoteCopyPath);
		checkFileIsAlive(remoteCopyPath2);
		checkFileIsAlive(remoteCopyPath3);
	//remove files
			removeRemoteOriginalFile();
			removeCopiedFile(remoteCopyPath, "test/SoftCopy1/");
			removeCopiedFile(remoteCopyPath2, "test/SoftCopy2/");
			removeCopiedFile(remoteCopyPath3, "test/SoftCopy3/");
			removeLocalFile(newFilePath);
			removeLocalFile(newFilePath2);

	}
	
	
	/**
	 * Utility test for cleaning the remote file from backend
	 * @throws RemoteBackendException
	 */
	@Test
	public void removeRemoteTemporaryFiles() throws RemoteBackendException {
		removeLocalFile(newFilePath);
		removeLocalFile(newFilePath2);
		removeRemoteOriginalFile();
		removeCopiedFile(remoteCopyPath, "test/copyDir/");
		
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
//		client.removeDir().RDir("test/img");
		List<StorageObject> linkList=client.showDir().RDir("test/img");
		assertTrue(linkList.isEmpty());
	}
	
	private void removeCopiedFile(String link, String dir) {
		System.out.println("remove  file at: "+link);
		client.remove().RFile(link);
		System.out.println("show dir: "+dir);
//		client.removeDir().RDir(dir);
		List<StorageObject> list=client.showDir().RDir(dir);
		assertTrue(list.isEmpty());
	}

}