package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.Before;
import org.junit.Test;

public class CountingTest {

	private String owner="roberto.cirillo";//"valentina.marioli";//"roberto.cirillo";
	private String remotePath="/test/img/CostaRica1.jpg";
	private String absoluteLocalPath;
	private IClient client;
	private String scope="/gcube/devsec/devVRE"; //"/d4science.research-infrastructures.eu"; // "/d4science.research-infrastructures.eu"; //"/d4science.research-infrastructures.eu"; //"/CNR.it/ISTI";//
	private String serviceClass="org.gcube.portlets.user";//"JUnitTest";
	private String serviceName="test-home-library";//"StorageManager";
	private String id;
	
	@Before
	public void getClient() throws RemoteBackendException{
		ScopeProvider.instance.set(scope);
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.PUBLIC, MemoryType.PERSISTENT).getClient();
			assertNotNull(client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void getFolderCount(){
		String folderCount=client.getFolderTotalItems().RDir(remotePath);
		assertNotNull(folderCount);
	}
	
	@Test
	public void getFolderVolume(){
		String folderVolume=client.getFolderTotalVolume().RDir(remotePath);
		assertNotNull(folderVolume);
	}

	@Test
	public void getUserCount(){
		String userItems=client.getUserTotalItems();
		assertNotNull(userItems);
		System.out.println(owner+" items: "+userItems);
	}
	
	@Test
	public void getUserVolume(){
		String userVolume=client.getTotalUserVolume();
		assertNotNull(userVolume);
		System.out.println(owner+" volume: "+userVolume);
	}
	
//	@After 
//	public void sleep(){
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
