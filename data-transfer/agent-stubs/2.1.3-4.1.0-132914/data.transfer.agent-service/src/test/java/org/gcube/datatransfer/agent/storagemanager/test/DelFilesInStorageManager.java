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

public class DelFilesInStorageManager {

	private IClient client;
	private String serviceClass="data-transfer";
	private String serviceName="scheduler-portlet";
	private String owner="testing";
	private String accessType="shared";

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

	@Test
	public void testDeleteFolder(){
		try{
		String remoteFolder="/";
		client.removeDir().RDir(remoteFolder);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		/*String finalResult="";
		finalResult=showDir("/",finalResult);
		System.out.println("ShowDir:\n"+finalResult);*/
		
	}
	
	
	//@Test
	public void testDeleteFile(){
		try{
		String remoteFile="/test/empty";
		String res = client.remove().RFile(remoteFile);
		
		System.out.println("res="+res);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		/*String finalResult="";
		finalResult=showDir("/",finalResult);
		System.out.println("ShowDir:\n"+finalResult);*/
		
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
