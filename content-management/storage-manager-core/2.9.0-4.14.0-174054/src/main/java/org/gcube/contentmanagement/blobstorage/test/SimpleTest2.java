package org.gcube.contentmanagement.blobstorage.test;

import java.util.List;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;



public class SimpleTest2 {
	
	public static void main(String[] args) throws RemoteBackendException{		
		String[] server=new String[]{"146.48.123.73","146.48.123.74" };

		IClient client=new ServiceEngine(server, "rcirillo", "cnr", "private", "rcirillo");		
//		String localFile="/home/rcirillo/FilePerTest/CostaRica.jpg";
		String remoteFile="/img/shared9.jpg";
		String newFile="/home/rcirillo/FilePerTest/repl4.jpg";
		client.get().LFile(newFile).RFile(remoteFile);
		List<StorageObject> list=client.showDir().RDir("/img/");
		for(StorageObject obj : list){
			System.out.println("obj found: "+obj.getName());
		}
		String uri=client.getUrl().RFile(remoteFile);
		System.out.println(" uri file: "+uri);
	}
}
