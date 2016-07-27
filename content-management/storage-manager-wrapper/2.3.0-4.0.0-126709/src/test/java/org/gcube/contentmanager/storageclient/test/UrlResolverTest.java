package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UrlResolverTest {

	private IClient client;
	private static String serviceName="TestStorage";
	private static String serviceClass="UrlTest";
	private static String owner="cirillo";
	private static String scope="/gcube/devsec";//"/CNR.it/ISTI";//"/gcube/devsec";
	private String localPath="src/test/resources/CostaRica1.jpg";
	private String remotePath="/test/img4/CostaRicaine.jpg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	
	@Before
	public void init() throws RemoteBackendException{
		ScopeProvider.instance.set(scope);
		 try {
			client = new StorageClient(serviceClass, serviceName, owner , AccessType.SHARED).getClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLocalResources();
		String id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
	}
	
	
	@Test
	public void getUrl() throws RemoteBackendException {
		String url=client.getUrl().RFile(remotePath);
		System.out.println("url generated: "+url);
	}
	
	@After
	public void removeRemoteFile() throws RemoteBackendException{
		String id=client.remove().RFile(remotePath);
		List<StorageObject> list=client.showDir().RDir("test/img4");
		assertTrue(list.isEmpty());
	}
	
	
	private void setLocalResources() {
		absoluteLocalPath=new File(localPath).getAbsolutePath();
		String dir=new File(absoluteLocalPath).getParent();
		newFilePath=dir+"/testJunit.jpg";
		absoluteLocalPath=new File(localPath).getAbsolutePath();
	}

}
