package org.gcube.data.analysis.tabulardata.operation.comet;

import java.util.UUID;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;

public class TestCommon {

	static String getMappingFileId(){
		// send mapping to storage
		
				StorageClient client = new StorageClient(ImportCodeListMappingFactory.class.getName(), ImportCodeListMappingFactory.class.getSimpleName(), ImportCodeListMappingFactory.class.getName(), AccessType.PUBLIC);
				IClient icClient = client.getClient();
				return icClient.put(true).LFile(ClassLoader.getSystemResourceAsStream("mapping.xml")).RFile(UUID.randomUUID().toString());
	}
	
	
}
