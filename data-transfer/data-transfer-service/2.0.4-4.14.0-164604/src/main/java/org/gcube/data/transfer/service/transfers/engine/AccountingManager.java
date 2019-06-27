package org.gcube.data.transfer.service.transfers.engine;

import org.gcube.data.transfer.service.transfers.engine.impl.AccountingManagerImpl;

public interface AccountingManager {

	public String createNewRecord();
//	public StorageUsageRecord getById(String id);
	public void account(String toAccountRecordId);	
	
	
	public void setSuccessful(String id,boolean succesfull); 
	public void setRead(String id);
	public void setCreate(String id);
	public void setDelete(String id);
	public void setUpdate(String id);
	public void setResourceURI(String id,String uri);
	public void setVolumne(String id, long volume);
	public void setMimeType(String id,String mimeType);
	
	
	
//	usageRecord.setOperationResult(TEST_OPERATION_RESULT);
	//		usageRecord.setResourceURI(new URI(TEST_RESOURCE_URI));
	//		usageRecord.setOperationType(AbstractStorageUsageRecord.OperationType.READ);
	//		usageRecord.setDataVolume(generateRandomLong(MIN_DATA_VOLUME, MAX_DATA_VOLUME));
	//		usageRecord.setQualifier("image/png");
	
	public static AccountingManager get() {
		return AccountingManagerImpl.get();
	}
}
