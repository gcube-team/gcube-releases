package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.test.utils.Costants;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the uri creation without payload
 * @author Roberto Cirillo
 *
 */
public class UrlResolverByIdTest {

	private IClient client;
	private static String serviceName="TestStorage";
	private static String serviceClass="UrlTest";
	private static String owner="cirillo";
//	private static String scope="/gcube/devsec";//"/d4science.research-infrastructures.eu";//"/gcube/devsec";//"/CNR.it/ISTI";//"/gcube/devsec";
	private String localPath="src/test/resources/CostaRica1.jpg";
	private String localNewPath="src/test/resources/CostaRicaMove.jpg";
	private String remotePath="/Uritest/img/CostaRicaine.jpg";
	private String newPath="/Uritest/img5/CostaMove.jpg";
	private String absoluteLocalPath;
	private String newFilePath="src/test/resources";
	private String id;
	
	@Before
	public void init() throws RemoteBackendException{
		ScopeProvider.instance.set(Costants.DEFAULT_SCOPE_STRING);
		 try {
			client = new StorageClient(serviceClass, serviceName, owner , AccessType.SHARED, Costants.DEFAULT_MEMORY_TYPE).getClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLocalResources();
// remove the following line for running 	getUrlbyIdNotPaylloadCheck test	
		id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
	}
	
	
//	@Test
	public void getUrlbyIdNotPaylloadCheck() throws RemoteBackendException {
		String url=client.getUrl(true).RFile(remotePath);
		System.out.println("url generated: "+url);
		assertNotNull(url);
		id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		System.out.println("id retrieved: "+id);
		client.moveFile().from(remotePath).to(newPath);
		verifyUrl(url);
	}
	
	
	@Test
	public void getUrlbyId() throws RemoteBackendException {
//		id="5aa16dfe02cadc50bff0eaf1";//"5aa16dfe02cadc50bff0eaf7";//"5a0c1cddf1d47d0c2fea0c1c";//"5a0c1cddf1d47d0c2fea0c1c";//"5a056737f1d47d0c2fe1ccb8";//"5a048c4cf1d47d0c2fe10537";//client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		String url=client.getHttpUrl(true).RFile(id);
//		String url=client.getUrl().RFileById(id);
		System.out.println("httpUrl generated: "+url);
		assertNotNull(url);
//		id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		
		
//		System.out.println("id retrieved: "+id);
//		client.moveFile().from(remotePath).to(newPath);
//		verifyUrl(url);
	}
	
	
//	@Test
//	public void getUrlbyId() throws RemoteBackendException {
////		id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
//		String url=client.getHttpUrl().RFileById("5aa16dfe02cadc50bff0eaf7");
//		System.out.println("url generated: "+url);
//		assertNotNull(url);
////		url=url+"-VLT";
//		System.out.println("id retrieved: "+id);
//		client.moveFile().from(remotePath).to(newPath);
//		verifyUrl(url);
//	}
	
	@Test
	public void getUrlbyPath() throws RemoteBackendException {
		id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		String url=client.getUrl(true).RFile(remotePath);
		System.out.println("url generated: "+url);
		assertNotNull(url);
		System.out.println("id retrieved: "+id);
		client.moveFile().from(remotePath).to(newPath);
		verifyUrl(url);
		client.moveFile().from(newPath).to(remotePath);
	}
	
	@Test
	public void getFilebyEncryptedId() throws RemoteBackendException {
		id=client.put(true).LFile(absoluteLocalPath).RFile(remotePath);
		String url=client.getUrl(true).RFile(remotePath);
		System.out.println("url generated: "+url);
		assertNotNull(url);
		String idEncrypted=url.substring(url.lastIndexOf(".org/")+5);
		System.out.println("id encrypted: "+id);
		client.get().RFileAsInputStream(remotePath);
		client.moveFile().from(remotePath).to(newPath);
		verifyUrl(url);
		client.moveFile().from(newPath).to(remotePath);
	}
	
	
	/**
	 * download the file and verify if the file exist
	 * @param url
	 */
	
	private void verifyUrl(String url) {
		Handler.activateProtocol();
		URL smsHome = null;
		try {
			smsHome = new URL(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		URLConnection uc = null;
		File f=null;
		try {
			uc = ( URLConnection ) smsHome.openConnection ( );
			InputStream is=uc.getInputStream();
			f=new File(localNewPath);
			FileOutputStream out=new FileOutputStream(f);
			byte buf[]=new byte[1024];
			int len=0;
			System.out.println("InputStream  "+is);
			while((len=is.read(buf))>0){
				  out.write(buf,0,len);
			}
		  out.close();
		  is.close();
		}catch(Exception e ){
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(f.exists());
	}

	@After
	public void removeRemoteFile() throws RemoteBackendException{
		String id=client.remove().RFile(remotePath);
		List<StorageObject> list=client.showDir().RDir("Uritest/img");
		assertTrue(list.isEmpty());
//		String id2=client.remove().RFile(newPath);
//		List<StorageObject> list2=client.showDir().RDir("Uritest/img5");
//		assertTrue(list.isEmpty());
		removeLocalFiles();
	}
	
	private void removeLocalFiles(){
		File f=new File(newFilePath);
		f.delete();
		assertFalse(f.exists());
		f=new File(localNewPath);
		f.delete();
		assertFalse(f.exists());

	}
	
	private void setLocalResources() {
		absoluteLocalPath=new File(localPath).getAbsolutePath();
		String dir=new File(absoluteLocalPath).getParent();
		newFilePath=dir+"/testJunit.jpg";
	}

}

