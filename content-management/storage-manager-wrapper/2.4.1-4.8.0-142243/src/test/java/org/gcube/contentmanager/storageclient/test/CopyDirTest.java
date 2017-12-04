package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CopyDirTest {

	private String owner="rcirillo";
// local file	
	private String localPath="src/test/resources/CostaRica1.jpg";
//remote files	
	private String remoteOriginalFilePath="/test/img/original.jpg";
	private String remoteOriginalFilePath2="/test/img/original2.jpg";
	private String remoteCopyFilePath="/test/copyImg/img/original.jpg";
	private String remoteCopyFilePath2="/test/copyImg/img/original2.jpg";
//remote directories	
	private String remoteOriginalDirPath="/test/img/";
	private String remoteCopyDirPath="/test/copyImg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube/devsec";//"/d4science.research-infrastructures.eu/FARM";//"/CNR.it/ISTI";//"/gcube"; // "/d4science.research-infrastructures.eu/FARM/VTI";//
	private String serviceClass="JUnitTest-CopyDir";
	private String serviceName="StorageManager";

	
	
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
		String id=client.put(true).LFile(absoluteLocalPath).RFile(remoteOriginalFilePath);
		assertNotNull(id);
		id=client.put(true).LFile(absoluteLocalPath).RFile(remoteOriginalFilePath2);
		assertNotNull(id);
		client.copyDir().from(remoteOriginalDirPath).to(remoteCopyDirPath);
	}

	/**
	 * Check the integrity of the remote copy folder after the  original copy folder's cancellation
	 * @throws RemoteBackendException
	 */
	@Test
	public void checkOriginalFolderTest() throws RemoteBackendException {
		client.get().LFile(newFilePath).RFile(remoteOriginalFilePath);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		client.get().LFile(newFilePath).RFile(remoteOriginalFilePath2);
		f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		client.removeDir().RDir(remoteOriginalDirPath);
		client.get().LFile(newFilePath).RFile(remoteCopyFilePath);
		f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		client.get().LFile(newFilePath).RFile(remoteCopyFilePath2);
		f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		client.copyDir().from(remoteCopyDirPath).to(remoteOriginalDirPath);

 	}

	/**
	 * Check the integrity of the original copy folder after the  remote copy folder's cancellation
	 * @throws RemoteBackendException
	 */
	@Test
	public void checkCopiedFolderTest() throws RemoteBackendException {
		client.get().LFile(newFilePath).RFile(remoteCopyFilePath);
		File f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		client.get().LFile(newFilePath).RFile(remoteCopyFilePath2);
		f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		client.removeDir().RDir(remoteCopyDirPath);
		client.get().LFile(newFilePath).RFile(remoteOriginalFilePath);
		f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		client.get().LFile(newFilePath).RFile(remoteOriginalFilePath2);
		f =new File(newFilePath);
		System.out.println("path new File downloaded: "+f.getAbsolutePath());
		assertTrue(f.exists());
		removeLocalFile();
		client.copyDir().from(remoteOriginalDirPath).to(remoteCopyDirPath);
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
	
	@After
	public void deleteRemoteDir(){
		client.removeDir().RDir(remoteCopyDirPath);
		client.removeDir().RDir(remoteOriginalDirPath);
	}

}
