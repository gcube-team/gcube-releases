package org.gcube.datatransfer.agent.storagemanager.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CreateNewFolder {
	private IClient client;
	private String serviceClass="data-transfer";
	private String serviceName="scheduler-portlet";
	private String owner="testing";
	private String accessType="shared";
	
	String path="desiredFolder1/desiredFolder2/";

	@Before
	public void setUp() throws Exception {
		GCUBEScope scope = GCUBEScope.getScope("/gcube/devsec");
		try {
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.valueOf(accessType.toUpperCase()), scope.toString()).getClient();
		} catch (Exception e) {		e.printStackTrace();
		}
		CreateLocalSources.createFile(path+"empty");
	}
	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateNewFolder(){

		client.put(true).LFile(path+"empty").RFile("/"+path+"empty");
		client.remove().RFile(path+"empty");


		String finalResult="";
		String rootPath="/";
		finalResult=showDir(rootPath,finalResult);
		System.out.println("ShowDir:\n"+finalResult);

		CreateLocalSources.removeFileAndFolders(path+"empty");
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
