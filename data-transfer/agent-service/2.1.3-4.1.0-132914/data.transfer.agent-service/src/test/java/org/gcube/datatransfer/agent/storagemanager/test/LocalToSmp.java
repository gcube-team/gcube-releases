package org.gcube.datatransfer.agent.storagemanager.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LocalToSmp {
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
	public void testLocalToSmp(){
		InputStream streamIn=null;
		OutputStream streamOut=null;
		try{
			String input="file:///tmp/testFolder/Wikipedia_logo_silver.png";
			FileObject inputFile = TransferUtils.prepareFileObject(input);
			streamIn = inputFile.getURL().openConnection().getInputStream();

			String output=client.getUrl().RFile("/test/Wikipedia");
			FileObject outputFile = TransferUtils.prepareFileObject(output);
			streamOut = outputFile.getURL().openConnection().getOutputStream();

			if(streamIn==null)System.out.println("streamIn==null");
			else streamIn.close();
			if(streamOut==null)System.out.println("streamOut==null");
			else streamOut.close();
			
			
			// the outputStream is not supported from the storage manager 
			//we can't use the CopyStreamHandler
			
			//.........
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
