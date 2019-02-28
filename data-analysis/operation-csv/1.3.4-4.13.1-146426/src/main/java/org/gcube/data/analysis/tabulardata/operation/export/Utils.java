package org.gcube.data.analysis.tabulardata.operation.export;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.tabulardata.operation.export.csv.Constants;

public class Utils {

	public static IClient getStorageClient(){
		IClient client = new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, "TDM", AccessType.SHARED).getClient();
		return client;
	}
	
}
