package org.gcube.data.analysis.statisticalmanager.test;

import java.io.File;

import org.apache.commons.io.IOUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPUrl;
import org.gcube.data.analysis.statisticalmanager.stubs.storage.RemoteStorage;






public class TestStorage {
	
	public static void main(String args[]) throws  Exception {
		ScopeProvider.instance.set("/gcube");
		RemoteStorage stg=new RemoteStorage();
		File f=new File("src/test/resources/storage.txt");
		System.out.println("Uploading ...");
		
		String id=stg.storeFile(f,true);
		System.out.println("Remote ID : "+id);
		String uri=stg.getUri(id);		
		System.out.println("URI : "+uri);
		System.out.println("Copying stream..");
		IOUtils.copy(new SMPUrl(uri).openConnection().getInputStream(), System.out);
		System.out.println("Importing file..");
		File dest=File.createTempFile("SMP", "");
		stg.downloadFile(id, dest.getAbsolutePath());
		System.out.println("Downloaded at "+dest.getAbsolutePath());
		System.out.println("Deleting remote..");
		stg.deleteRemoteFile(id);
	}
}
