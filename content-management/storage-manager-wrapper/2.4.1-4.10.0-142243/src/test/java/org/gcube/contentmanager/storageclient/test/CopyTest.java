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

public class CopyTest {

	
	private String owner="rcirillo";
	private String localPath="src/test/resources/CostaRica1.jpg";
	private String remoteOriginalFilePath="/test/img/original2.jpg";
	private String remoteCopyPath="/test/copyDir/link.jpg";
	private String remoteCopyPath2="/test/copyDirCopy/link.jpg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube/devsec"; //"/d4science.research-infrastructures.eu/gCubeApps";//"/d4science.research-infrastructures.eu/FARM";// "/d4science.research-infrastructures.eu/FARM/VTI";//
	private String serviceClass="CopyTest";
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
	 * 
	 * @throws RemoteBackendException
	 */
	@Test
	public void removeCopiedFileTest() throws RemoteBackendException {
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


	private void removeRemoteOriginalFile() {
		client.remove().RFile(remoteOriginalFilePath);
		List<StorageObject> linkList=client.showDir().RDir("test/img");
		assertTrue(linkList.isEmpty());
	}
	
	private void removeCopiedFile(String link, String dir) {
		System.out.println("remove link file at: "+link);
		client.remove().RFile(link);
		System.out.println("show dir: "+dir);
		List<StorageObject> list=client.showDir().RDir(dir);
		assertTrue(list.isEmpty());
	}

}
