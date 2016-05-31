package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class UploadsTest {

	private String owner="rcirillo";
	private String localPath="src/test/resources/CostaRica.jpg";//"src/test/resources/empty.txt";//"src/test/resources/CostaRica1.jpg";
	private String remotePath="/tests/img/CostaRica1.jpg";//"/tests/img/empty.txt";//
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private IClient client;
	private String scope="/gcube/devsec";//"/d4science.research-infrastructures.eu/gCubeApps"; //"/CNR.it";////"/gcube/devsec";//""/CNR.it/ISTI";//"/gcube";//"/gcube/devNext/NextNext";//
	private String serviceClass="JUnitTest";
	private String serviceName="StorageManager";
	
	@Before
	public void getClient(){
		ScopeProvider.instance.set(scope);
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, MemoryType.VOLATILE).getClient();
			assertNotNull(client);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLocalResources();
	}


	@Test
	public void uploadByPath() throws RemoteBackendException {
		String id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		System.out.println("UploadByPath id: "+id);
		client.get().LFile(absoluteLocalPath).RFile(id);
		assertTrue(new File(absoluteLocalPath).exists());
		id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		System.out.println("UploadByPath again id: "+id);
		System.out.println("download file test by id");
		client.get().LFile(absoluteLocalPath).RFile(id);
		assertTrue(new File(absoluteLocalPath).exists());
		assertNotNull(id);
		removeRemoteFile();
	}
	
//	@Test
	public void uploadwithMimeType() throws RemoteBackendException {
		String id=client.put(true, "image/jpeg").LFile(absoluteLocalPath).RFile(remotePath);
		System.out.println("UploadByPath id: "+id);
		assertNotNull(id);
		removeRemoteFile();
	}

//	@Test
	public void uploadByPathWithBAckendReturnedTest() throws RemoteBackendException {
		String id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath, true);
		System.out.println("UploadByPath id+backendType: "+id);
		assertNotNull(id);
		removeRemoteFile();
	}
	
//	@Test
	public void uploadByInputStream(){
		InputStream is=null;
		try {
			is=new FileInputStream(new File(absoluteLocalPath));
			client.put(true).LFile(is).RFile(remotePath);
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		removeRemoteFile();
	}
	
	
//	@Test
	public void uploadByOutputStream() throws IOException{
		OutputStream out=client.put(true).RFileAsOutputStream(remotePath);
		assertNotNull(out);
		System.out.println("outstream returned: "+out);
		FileInputStream fin=null;
		try {
			fin=new FileInputStream(new File(absoluteLocalPath));
			int c;
	        while ((c = fin.read()) != -1) {
	            out.write(c); }
	    } catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
	         if (fin != null) {
	            fin.close();
	        }
		}
		removeRemoteFile();
	}
	


	
	private void removeRemoteFile() throws RemoteBackendException{
		client.removeDir().RDir("tests/img");
		List<StorageObject> list=client.showDir().RDir("tests/img");
		assertTrue(list.isEmpty());
	}

	@After
	public void removeLocalFile(){
		File f=new File(newFilePath);
		f.delete();
		assertFalse(f.exists());
	}

	private void setLocalResources() {
		absoluteLocalPath=new File(localPath).getAbsolutePath();
		String dir=new File(absoluteLocalPath).getParent();
		newFilePath=dir+"/testJunit.jpg";
		absoluteLocalPath=new File(localPath).getAbsolutePath();
	}
}
