package org.apache.commons.vfs2.provider.test;

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
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;

public class SmpVfs {
	
	public static void main(String args[]){
		
		testResolve();
		
		/*try {
			Test1();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	//	Test2();
		
	//	Test3();
		/*
		try {
			testGetFromURISMP();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

	public static void testResolve(){
		String str = "smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXe8yVPqyEgEX301SBxBimaW2cbB+i5RUOH+ENSe5RIsJFOBij7Ig+jwlmp4jGP9GeLZhHUvtLsbgrhYk9Ddgbr20LHoq2uYsiPukNgpqN0/Dw==";
		//String str = "smp://andrea.manzi/Family%20-Apocynaceae-%20from%20BrazilianFlora.zip?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuF12imSlGUxkHCxl1J0uif8mIlmY5ha9WeAUHRAcNonDTzaTDpi6bmWNDInpP01lK7100s+1X/eNDUWNMQZdZbHMJfYMmmXptQ==";
		try {
			DefaultFileSystemManager defaultmanag= new DefaultFileSystemManager();
			defaultmanag.addProvider("smp", new SmpFileProvider());
			defaultmanag.setDefaultProvider(new UrlFileProvider());
			defaultmanag.init();	
			FileSystemOptions opts = new FileSystemOptions();
			FileObject file = defaultmanag.resolveFile(str,opts);
						
			System.out.println("url="+file.getURL());
			InputStream input = file.getContent().getInputStream();
			System.out.println("size="+file.getContent().getSize());
			if(input==null)System.out.println("inputstream=null");
			else System.out.println("inputstream="+input);
			
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void Test1() throws IOException{
		System.out.println("Test1: Creating a SMP provider and reading an existing file from SM. " +
				"Copy that file to a local one located in /home/nick/test/outPutFileNew..");
		String uri = "smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuP4Q1J7lEiwkU4GKPsiD6PDRVcT4QAqTEy5hSIbr6o4Y";
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
			System.out.println("fileObject.getURL()="+fileObject.getURL());
			
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

	//	System.out.println("print url..");
	//	print(url);
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

	public static void Test2(){
		System.out.println("Test2: Creating a SMP provider and resolving an existing file from SM. " +
				"Copy that file to a remote one located in a ftp server..");
		
		String input = "smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuP4Q1J7lEiwkU4GKPsiD6PDRVcT4QAqTEy5hSIbr6o4Y";
		String output="ftp://d4science:fourD_314@ftp.d4science.org/nickTest/testFile";
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
			outputObject = VFS.getManager().resolveFile(output,opts);
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
	
	public static void Test3(){
		System.out.println("Test3: Decrypting a SMP url ... ");
		
		String url = "smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuP4Q1J7lEiwkU4GKPsiD6PDRVcT4QAqTEy5hSIbr6o4Y";
		String[] parts = url.split("\\?");
		DecryptSmpUrl.decrypt(parts[1]);
		System.out.println("ServiceClass="+DecryptSmpUrl.serviceClass+"\n"+
				"ServiceName="+DecryptSmpUrl.serviceName+"\n"+
				"Owner="+DecryptSmpUrl.owner+"\n"+
				"AccessType="+DecryptSmpUrl.accessType+"\n"+
				"Scope="+DecryptSmpUrl.scopeType+"\n");
	}
	
	public static void print(URL url){
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
	public static void testGetFromURISMP() throws IOException{
		System.out.println("testGetFromURISMP: Activate SMP protocol with the Handler from " +
				"Storage Manager library, open a smp url and copy that file to a local one ..(/home/nick/test/outPutFile2) ");
		
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
