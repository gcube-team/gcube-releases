package org.gcube.datatransfer.agent.storagemanager.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import junit.framework.Assert;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StorageManager {

	private IClient client;
	String localFile="/home/nick/test/testFile";
	String localImage="/home/nick/test/test.png"; // size 393642
	String remoteImage="/temporary/test.png"; // size 393642
	
	String remoteFile="/temporary/testFile";
	String remoteFile2="/temporary/testFolder/testFile";
	String remoteFile3="/temporary/testFolder/folder2/testFile";

	String newFile="/home/nick/test/testFile2";
	String newFile2="/home/nick/test/testFile3";

	@Before
	public void setUp() throws Exception {
		GCUBEScope scope = GCUBEScope.getScope("/gcube");

	try {
			client=new StorageClient("TestClass", "TestService", "Nick", AccessType.SHARED, scope.toString()).getClient();
	} catch (Exception e) {		e.printStackTrace();
	}
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
	public void testPut() {
		Assert.assertNotNull(client.put(true).LFile(localFile).RFile(remoteFile));
		
	}
	@Test
	public void testPutImage() {
		Assert.assertNotNull(client.put(true).LFile(localImage).RFile(remoteImage));
		
	}
//	@Test
	public void testGet(){
		String result = client.get().LFile(newFile).RFile(remoteFile);
		System.out.println(result);
		Assert.assertTrue(new File(newFile).exists())  ;
	}
	@Test
	public void testGetSize(){
		long result = client.getSize().RFile(remoteImage);
		System.out.println("size="+result);

	}
	@Test
	public void testShowDir(){
		//Assert.assertNotNull(client.put(true).LFile(localFile).RFile(remoteFile2));
		//Assert.assertNotNull(client.put(true).LFile(localFile).RFile(remoteFile3));
		String finalResult="";
		finalResult=showDir("/",finalResult);
		System.out.println("ShowDir:\n"+finalResult);
		
		System.out.println("remote image url="+client.getUrl().RFile("/temporary/test.png"));
	}
	
	public String showDir(String path,String finalResult){
	//	System.out.println("Dir:"+path);
		finalResult=finalResult+"Dir:"+path+"\n";
		List<StorageObject> result = client.showDir().RDir(path);
		for(StorageObject obj:result){		
			if (!obj.isDirectory()){
				//System.out.println("File:"+obj.getName());
				finalResult=finalResult+"File:"+path+obj.getName()+"\n";
			}
			else {				
				finalResult=showDir(path+obj.getName()+"/",finalResult);
			}
		}
		return finalResult;
	}
	
	
	
	//@Test
	public void testGetFromURISMP() throws IOException{
		Handler.activateProtocol();
		URL smsHome = null;
		try {
			//smsHome = new URL("smp://temporary/file?ServiceClass=TestClass&ServiceName=TestName&owner=AndreaManzi&scope=/gcube&AccessType=shared" );
			smsHome = new URL("smp://temporary/testFile?5ezvFfBOLqYJ+M0vmF5az+aJSmGtP6VXVMReFhgdGEZifATnvOQ3QmBUhER21xAlAOF+gORZy71SbB0T8Oz7PJM9M3uFaAke1SXl7QQHOpVCbP5YCTXMwhpmz+fhygs3" );

		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URLConnection uc = null;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			uc = ( URLConnection ) smsHome.openConnection ( );
			in = uc.getInputStream();
	
			
			/*InputStreamReader is = new InputStreamReader(in);
			BufferedReader br=new BufferedReader(is);
			String read =br.readLine();
			StringBuilder sb=new StringBuilder();
			while(read!=null){
				sb.append(read);
				read=br.readLine();
			}			
			System.out.println("what we read:\n"+sb.toString());*/
			
			
			out =  new FileOutputStream( newFile2);
			int nextChar;
			while ( ( nextChar = in.read() ) != -1  ) 
				out.write(nextChar );
			out.flush();
		}catch (Exception e){e.printStackTrace();}
		finally{
			out.close();
			in.close();
		}
		Assert.assertEquals(new File(localFile).length(), new File(newFile2).length());
		
		
	}

//	@Test
	public void testRemoveFile() {	
		Assert.assertNotNull(client.remove().RFile(remoteFile));
	}

//	@Test
	public void testRemoveDir(){
		List<StorageObject> list  = client.removeDir().RDir("/temporary");
		Assert.assertNotNull(list);
		for(StorageObject object: list){
			System.out.println(object.getName());
			Assert.assertNotNull(object);
		}


	}


}
