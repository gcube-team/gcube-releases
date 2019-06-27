package org.gcube.data.analysis.statisticalmanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils.ScopeBean;

public class RemoteStorageTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException, FileNotFoundException, IOException {
//		Handler.activateProtocol();
//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
//		File destination=File.createTempFile("imported", ".tmp");
//		String url="smp://data.gcube.org/huGnYhS/ieEULbezvqbrNZzNHVyfpYnAGmbP5+HKCzc=";
//		IOUtils.copy(new RemoteStorage().getStreamByUrl(url),new FileOutputStream(destination));
//		System.out.println("Imported to "+destination.getAbsolutePath());
		
		ScopeUtils.setAuthorizationSettings(new ScopeBean("/gcube/devsec",null));
		
		
		String pathToFile="/home/fabio/Desktop/OSCommand.txt";
		
		String ID=getStorage().getClient().put(true).LFile(pathToFile).RFile(UUID.randomUUID().toString());
		
		System.out.println("ID IS "+ID);
		ScopeUtils.setAuthorizationSettings(new ScopeBean("/gcube","/gcube"));
		
		System.out.println(getStorage().getClient().getUrl().RFile(ID));
	}

	
	private static StorageClient getStorage(){
		return new StorageClient("org.gcube.data.analysis.statisticalmanager",
				ServiceContext.SERVICE_NAME, ServiceContext.SERVICE_NAME, AccessType.SHARED); 
	}
}
