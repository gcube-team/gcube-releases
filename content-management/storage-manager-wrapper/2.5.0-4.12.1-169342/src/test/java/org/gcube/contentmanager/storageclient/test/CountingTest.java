package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.test.utils.Costants;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.Before;
import org.junit.Test;

public class CountingTest {

	private String owner="daniel.williams";//"scarponi";//"alessandro.pieve";//"roberto.cirillo";//"valentina.marioli";//"roberto.cirillo";
	private String remotePath="/test/img/CostaRica1.jpg";
	private String remoteDir="/test/img/";
//	private String absoluteLocalPath;
	private IClient client;
//	private String scope="/gcube/devsec";//"/d4science.research-infrastructures.eu"; //"/gcube/devsec/devVRE"; //" "/d4science.research-infrastructures.eu"; //"/d4science.research-infrastructures.eu"; //"/CNR.it/ISTI";//
	private String serviceClass="JUnitTest";//"org.gcube.portlets.user";//
	private String serviceName="StorageManager";//"test-home-library";//
//	private String id;
	
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
		
	}
	
	@Test
	public void getFolderCount(){
		String folderCount=client.getFolderTotalItems().RDir(remoteDir);
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
