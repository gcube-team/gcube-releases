package org.gcube.datatransfer.agent.storagemanager.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.DecryptSmpUrl;
import org.apache.commons.vfs2.provider.SmpFileProvider;
import org.apache.commons.vfs2.provider.url.UrlFileProvider;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.junit.Before;
import org.junit.Test;

public class SmpVfs {
	@Before
	public void setUp() throws Exception {
		GCUBEScope scope = GCUBEScope.getScope("/gcube");
	}


	//@Test
	public void Test1() throws IOException{
		String uri = "smp://home/nick/Downloads/test/Wikipedia_logo_silver.png?5ezvFfBOLqYJ+M0vmF5az+aJSmGtP6VXVMReFhgdGEZifATnvOQ3QmBUhER21xAlAOF+gORZy71SbB0T8Oz7PCKju47SHN3J1WAkxzwIjR1/XO2bqsokgB5v1H/QUQgN";
		
		FileSystemOptions opts =new  FileSystemOptions();
		
		// Root directory set to user home
		//SmpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
		FileObject fileObject=null;				
		try {
			
			SmpFileProvider provider=new SmpFileProvider();
			UrlFileProvider defaultprovider = new UrlFileProvider();
			
			DefaultFileSystemManager defaultmanag= new DefaultFileSystemManager();
			defaultmanag.addProvider("smp", provider);
			defaultmanag.setDefaultProvider(defaultprovider);
			defaultmanag.init();
			
			fileObject = defaultmanag.resolveFile(uri,opts);
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
		if(fileObject==null){
			System.out.println("fileObject=null");
			return;
		}
		try {
			System.out.println("fileObject.getURL()="+fileObject.getURL()+"\n"+
					"fileObject.getName()="+fileObject.getName()+"\n"+
					"fileObject.getName().getBaseName()="+fileObject.getName().getBaseName()+"\n");
			
		} catch (FileSystemException e1) {
			e1.printStackTrace();
		}
		URL url = null;
		
		try {
			url = fileObject.getURL();
		} catch (FileSystemException e1) {
			e1.printStackTrace();
		}
		//URL second = new URL("smp://temporary/testFile?5ezvFfBOLqYJ+M0vmF5az+aJSmGtP6VXVMReFhgdGEZifATnvOQ3QmBUhER21xAlAOF+gORZy71SbB0T8Oz7PJM9M3uFaAke1SXl7QQHOpVCbP5YCTXMwhpmz+fhygs3" );

		System.out.println("print url..");
		print(url);
		//System.out.println("print second url");
		//print(second);
		if(true)return;
		URLConnection uc = null;
		InputStream in = null;
		File outputFile = new File("/home/nick/test/outPutFileNew");
		FileOutputStream out = null;
		try {
			uc = ( URLConnection ) url.openConnection ( );
			in = uc.getInputStream();
					
			out =  new FileOutputStream( outputFile);
			int nextChar;
			while ( ( nextChar = in.read() ) != -1  ) 
				out.write(nextChar );
			out.flush();
		}catch (Exception e){e.printStackTrace();}
		finally{
			out.close();
			in.close();
		}
		
		
	}
	@Test
	public void Test2(){
		String output = "smp://devtest5/Wikipedia_logo_silver.png?5ezvFfBOLqYJ+M0vmF5az6HIxe1njPLMZ4BC5NS/+Ow/Ya39qIC+cuPieuJCqmopP6qYi6HVeNMQotCWBfatmv4Q1J7lEiwkNMDuLTfe4rIQELLbyLZ/GesZiduaN+bqFELF3+F57Kk=";
		String input="smp://devtest4/Wikipedia_logo_silver.png?5ezvFfBOLqYJ+M0vmF5az6HIxe1njPLMZ4BC5NS/+Ow/Ya39qIC+cuPieuJCqmopP6qYi6HVeNMQotCWBfatmv4Q1J7lEiwkNMDuLTfe4rIQELLbyLZ/GesZiduaN+bqFELF3+F57Kk=";
		FileSystemOptions opts =new  FileSystemOptions();
		
		// Root directory set to user home
		//SmpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
		FileObject inputObject=null;	
		FileObject outputObject=null;	
		try {
			
			SmpFileProvider provider=new SmpFileProvider();
			UrlFileProvider defaultprovider = new UrlFileProvider();
			
			DefaultFileSystemManager defaultmanag= new DefaultFileSystemManager();
			defaultmanag.addProvider("smp", provider);
			defaultmanag.setDefaultProvider(defaultprovider);
			defaultmanag.init();
			
			inputObject = defaultmanag.resolveFile(input,opts);
			outputObject = defaultmanag.resolveFile(output,opts);
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
		if(inputObject==null || outputObject==null){
			System.out.println("inputObject=null or outputObject=null");
			return;
		}
		
		
		try {
			System.out.println("Copy file from URL "+ inputObject.getURL() + " to : " +outputObject.getURL());
			outputObject.copyFrom(inputObject, Selectors.SELECT_SELF);
			System.out.println("File succesfully copied to "+ outputObject.getURL());
			
		} catch (FileSystemException e2) {
			e2.printStackTrace();
		}		
	}
	
	//@Test
	public void Test3(){
		String url = "smp://temporary/testFile?5ezvFfBOLqYJ+M0vmF5az+aJSmGtP6VXVMReFhgdGEZifATnvOQ3QmBUhER21xAlAOF+gORZy71SbB0T8Oz7PJM9M3uFaAke1SXl7QQHOpVCbP5YCTXMwhpmz+fhygs3";
		String[] parts = url.split("\\?");
		DecryptSmpUrl.decrypt(parts[1]);
		System.out.println("ServiceClass="+DecryptSmpUrl.serviceClass+"\n"+
				"ServiceName="+DecryptSmpUrl.serviceName+"\n"+
				"Owner="+DecryptSmpUrl.owner+"\n"+
				"AccessType="+DecryptSmpUrl.accessType+"\n"+
				"Scope="+DecryptSmpUrl.scopeType+"\n");
	}
	
	public void print(URL url){
		System.out.println("getAuthority="+url.getAuthority()+"\n"+
				"getDefaultPort="+url.getDefaultPort()+"\n"+
				"getFile="+url.getFile()+"\n"+
				"getHost="+url.getHost()+"\n"+
				"getPath="+url.getPath()+"\n"+
				"getPort="+url.getPort()+"\n"+
				"getProtocol="+url.getProtocol()+"\n"+
				"getQuery="+url.getQuery()+"\n"+
				"getRef="+url.getRef()+"\n"+
				"getUserInfo="+url.getUserInfo()
				);
	}
	
	public void testGetFromURISMP() throws IOException{
		File outputFile = new File("/home/nick/test/outPutFile2");
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
			
				
			out =  new FileOutputStream( outputFile);
			int nextChar;
			while ( ( nextChar = in.read() ) != -1  ) 
				out.write(nextChar );
			out.flush();
		}catch (Exception e){e.printStackTrace();}
		finally{
			out.close();
			in.close();
		}
	//	Assert.assertEquals(new File(localFile).length(), new File(newFile2).length());
		
		
	}
}
