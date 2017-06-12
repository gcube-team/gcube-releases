package org.gcube.datatransfer.agent.storagemanager.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.DecryptSmpUrl;
import org.apache.commons.vfs2.provider.SmpFileProvider;
import org.apache.commons.vfs2.provider.url.UrlFileProvider;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BrowseStorageManager {

	private IClient client;
	private String serviceClass="data-transfer";
	private String serviceName="scheduler-portlet";
	private String owner="testing";
	private String accessType="private";

	@Before
	public void setUp() throws Exception {
		GCUBEScope scope = GCUBEScope.getScope("/gcube/devsec");
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.valueOf(accessType.toUpperCase()), scope.toString()).getClient();
		} catch (Exception e) {		e.printStackTrace();
		}
	}
	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testPutSomeFiles(){
		String localFile="/home/nick/test/test.png";
		String remoteFile="/temporary/newOne/image1.png";
	//	String remoteFile2="/temporary/nickFolder1/image2.png";
	//	String remoteFile3="/temporary/nickFolder2/image.png";
		client.put(true).LFile(localFile).RFile(remoteFile);
	//	client.put(true).LFile(localFile).RFile(remoteFile2);
	//	client.put(true).LFile(localFile).RFile(remoteFile3);
	}
	//@Test
	public void testPutSomeRemoteFiles(){
		String localFile="/home/nick/data-transfer-tmp";
		String remoteFile="/devtest18/test";
		client.put(true).LFile(localFile).RFile(remoteFile);
		System.out.println("url="+client.getUrl().RFile(remoteFile));
	}
	//@Test
	public void testCopyFileAndTransfer(){
		URL input = null;
		try {
			input = new URL("http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URLConnection con = null;
		InputStream streamIn = null;
		try {
			con = input.openConnection();
			streamIn = con.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		con.setConnectTimeout(20000);
		File tmpFile = new File("/tmp/testNick");
		FileOutputStream streamOut = null;
		try {
			streamOut = new FileOutputStream(tmpFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			IOUtils.copy(streamIn, streamOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			streamIn.close();
			streamOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String remoteFile="/devtest23/test";
		client.put(true).LFile(tmpFile.getAbsolutePath()).RFile(remoteFile);
		System.out.println("url="+client.getUrl().RFile(remoteFile));
	}
	//@Test
	public void testShowDir(){		
		String finalResult="";
		String rootPath=".";
		
		if(rootPath.startsWith("."))rootPath=rootPath.substring(1);
		if(!rootPath.startsWith("/"))rootPath="/"+rootPath;
		if(rootPath.endsWith("."))rootPath=rootPath.substring(0, rootPath.length()-1);
		if(!rootPath.endsWith("/"))rootPath=rootPath+"/";
		
		finalResult=showDir(rootPath,finalResult);
		System.out.println("ShowDir:\n"+finalResult);
		
	}
	
	//@Test
	public void testPutSomeRemoteFilesFromUris(){
		//uri ->download to localfile -> copy(upload) to remotefile
			String uri="http://upload.wikimedia.org/wikipedia/commons/6/6e/Wikipedia_logo_silver.png";
			File localFile=new File ("/home/nick/Wikipedia_logo_silverLoc.png");
			String remoteFile="/devTest4/Wikipedia_logo_silver.png";
			
			FileOutputStream streamOut = null;
			try {
				streamOut = new FileOutputStream(localFile);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			InputStream streamIn=null;
			try {
				FileObject in= VFS.getManager().resolveFile(uri);
				URLConnection con = in.getURL().openConnection();
				//URL uriS=new URL(uri);
				//URLConnection con = uriS.openConnection();
				streamIn=con.getInputStream();
				IOUtils.copy(streamIn, streamOut);
				streamIn.close();
				streamOut.close();
			} catch (FileSystemException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("local file size="+localFile.length());
			client.put(true).LFile(localFile.getAbsolutePath()).RFile(remoteFile);
			System.out.println("url="+client.getUrl().RFile(remoteFile));
		}
	@Test
	public void testTakingTheInputStream(){
		try{
		//String str = "smp://devtest20/test?5ezvFfBOLqYJ+M0vmF5az6HIxe1njPLMZ4BC5NS/+Ow/Ya39qIC+cuPieuJCqmopP6qYi6HVeNMQotCWBfatmv4Q1J7lEiwkNMDuLTfe4rIQELLbyLZ/GesZiduaN+bqFELF3+F57Kk=";
		
		String rfile="/devTest4/Wikipedia_logo_silver.png";
		InputStream inputstream = client.get().RFileAsInputStream(rfile);
		
		if(inputstream==null)System.out.println("inputstream==null");
		else System.out.println("inputstream!=null");
		System.out.println("url="+client.getUrl().RFile(rfile));
		
	}
	catch(Exception e){
			e.printStackTrace();
		}
	}
	//@Test
	public void testRetrievingTheRemoteFile1(){
		//String str = "smp://devtest20/test?5ezvFfBOLqYJ+M0vmF5az6HIxe1njPLMZ4BC5NS/+Ow/Ya39qIC+cuPieuJCqmopP6qYi6HVeNMQotCWBfatmv4Q1J7lEiwkNMDuLTfe4rIQELLbyLZ/GesZiduaN+bqFELF3+F57Kk=";
		File localFile=new File ("/home/nick/receivedFile");
		
		//String rfile="/temporary2/Wikipedia_logo_silver.png";
		String rfile= "devTest52/test";
		
		client.get().LFile(localFile.getAbsolutePath()).RFile(rfile);
		
	}
	
	//@Test
	public void testSome(){
		String str = "smp://devTest44/Wikipedia_logo_silver.png?5ezvFfBOLqYJ+M0vmF5az6HIxe1njPLMZ4BC5NS/+Ow/Ya39qIC+cuPieuJCqmopP6qYi6HVeNMQotCWBfatmv4Q1J7lEiwkNMDuLTfe4rIQELLbyLZ/GesZiduaN+bqFELF3+F57Kk=";
		String[] parts = str.split("\\?");
		InputStream streamIn = null;
		if(streamIn==null){
			System.out.println("streamIn is null in the first place so we receive the IClient manually");
			DecryptSmpUrl.decrypt(parts[1]);
			System.out.println("DecryptSmpUrl.scopeType="+DecryptSmpUrl.scopeType+"\n"+
					"DecryptSmpUrl.accessType="+DecryptSmpUrl.accessType+"\n"+
					"DecryptSmpUrl.owner="+DecryptSmpUrl.owner+"\n"+
					"DecryptSmpUrl.serviceClass="+DecryptSmpUrl.serviceClass+"\n"+
					"DecryptSmpUrl.serviceName="+DecryptSmpUrl.serviceName+"\n");
			GCUBEScope scope = GCUBEScope.getScope(DecryptSmpUrl.scopeType);
			IClient client = null;
			try {
				client = new StorageClient(DecryptSmpUrl.serviceClass, DecryptSmpUrl.serviceName, DecryptSmpUrl.owner, AccessType.valueOf(DecryptSmpUrl.accessType.toUpperCase()),scope.toString()).getClient();
			} catch (Exception e) {
				e.printStackTrace();
			}
			streamIn=client.get().RFileAsInputStream(parts[0].replaceFirst("smp://", ""));
		}
		if(streamIn==null){
			System.out.println("streamIn=null");
		}
		
		

	}
	public void printUrl(URL url){
		System.out.println("url.getAuthority()="+url.getAuthority()+"\n"+
				"url.getDefaultPort()="+url.getDefaultPort()+"\n"+
				"url.getFile()="+url.getFile()+"\n"+
				"url.getHost()="+url.getHost()+"\n"+
				"url.getPath()="+url.getPath()+"\n"+
				"url.getPort()="+url.getPort()+"\n"+
				"url.getProtocol()="+url.getProtocol()+"\n"+
				"url.getQuery()="+url.getQuery()+"\n"+
				"url.getRef()="+url.getRef()+"\n"+
				"url.getUserInfo()="+url.getUserInfo()+"\n");
	}

	public String showDir(String path,String finalResult){
		//	System.out.println("Dir:"+path);
		finalResult=finalResult+"Dir:"+path+"\n";
		List<StorageObject> result = client.showDir().RDir(path);
		for(StorageObject obj:result){		
			if (!obj.isDirectory()){
				
				//System.out.println("File:"+obj.getName());
				finalResult=finalResult+"File:"+path+obj.getName()+" - size="+client.getSize().RFile(path+obj.getName())+"\n";
			}
			else {				
				finalResult=showDir(path+obj.getName()+"/",finalResult);
			}
		}
		return finalResult;
	}	

}
