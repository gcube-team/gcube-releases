package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.Before;
import org.junit.Test;

public class LinkTest {

	private String owner="rcirillo";
	private String localPath="src/test/resources/CostaRica1.jpg";
	private String remoteOriginalFilePath="/test/img/original.jpg";
	private String remoteOriginalDirPath="/test/img";
	private String remoteLinkPath="/test/linkDir/link.jpg";
	private String remoteLinkPath2="/test/linkDirCopy/link.jpg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube/devsec";//"/CNR.it/ISTI";//"/gcube"; // "/d4science.research-infrastructures.eu/FARM/VTI";//
	private String serviceClass="JUnitTest-linkTest";
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
		
	}

	/**
	 * 2 links creation from a file
	 * @throws RemoteBackendException
	 */
	@Test
	public void linkTest() throws RemoteBackendException {
//first Link creation	
		client.linkFile().from(remoteOriginalFilePath).to(remoteLinkPath);
		client.get().LFile(newFilePath).RFile(remoteLinkPath);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
 //second link creation		
		client.linkFile().from(remoteLinkPath).to(remoteLinkPath2);
		client.get().LFile(newFilePath).RFile(remoteLinkPath2);
		f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLinkFiles();
//		removeOriginalFiles();
		removeLocalFile();
	}

	
//	@Test
	public void removeLinkFiles() throws RemoteBackendException{
// remove first link
		removeLink(remoteLinkPath, "test/linkDir/");
		checkOriginalFileIsAlive();
// remove second link
		removeLink(remoteLinkPath2, "test/linkDirCopy/");
		checkOriginalFileIsAlive();
//remove original file		
		removeRemoteOriginalFile();
	}

	
//	@Test
	public void removeOriginalFiles() throws RemoteBackendException{
// creation files
		linkTest();
// remove original file		
//		removeRemoteOriginalFile();
		removeRemoteOriginalDir();
		checkLinkFileIsAlive();
// remove first link
		removeLink(remoteLinkPath, "test/linkDir/");
// remove second link
		removeLink(remoteLinkPath2, "test/linkDirCopy/");
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

	private void checkLinkFileIsAlive() {
		String id=client.get().LFile(newFilePath).RFile(remoteLinkPath);
		System.out.println("id link is alive: "+id);
		assertNotNull(id);
		removeLocalFile();
	}

	private void removeRemoteOriginalFile() {
		client.remove().RFile(remoteOriginalFilePath);
		List<StorageObject> linkList=client.showDir().RDir("test/img");
		assertTrue(linkList.isEmpty());
	}
	
	private void removeRemoteOriginalDir() {
		client.removeDir().RDir(remoteOriginalDirPath);
	}
	
	private void removeLink(String link, String dir) {
		System.out.println("remove link file at: "+link);
		client.remove().RFile(link);
		System.out.println("show dir: "+dir);
		List<StorageObject> list=client.showDir().RDir(dir);
		assertTrue(list.isEmpty());
	}


}