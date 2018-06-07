package org.gcube.data.transfer.service.transfers.engine.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.accounting.datamodel.basetypes.AbstractStorageUsageRecord;
import org.gcube.accounting.datamodel.basetypes.AbstractStorageUsageRecord.OperationType;
import org.gcube.accounting.datamodel.usagerecords.StorageUsageRecord;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.data.transfer.service.transfers.engine.AccountingManager;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AccountingManagerImpl implements AccountingManager {

	private static AccountingManagerImpl instance=null;

	@Synchronized
	public static final AccountingManagerImpl get() {
		if(instance==null)instance=new AccountingManagerImpl();
		return instance;
	}

	private ConcurrentHashMap<String,StorageUsageRecord> records=new ConcurrentHashMap<>();


	@Override
	public String createNewRecord(){
		try {
			StorageUsageRecord newer=initRecord();
			log.debug("Created record : {} ",newer);
			records.put(newer.getId(),newer);
			return newer.getId();
		}catch(Throwable t) {
			log.warn("Unable to register new record ",t);
			return "fake";
		}
	}

	
	private StorageUsageRecord getById(String recordId) {
		try{
			return records.get(recordId); 
		}catch(Throwable t) {
			log.warn("Unable to locate Accountind record with id {} ",recordId,t);
			return null;
		}
	}

	@Override
	public void account(String toAccountRecordId) {
		StorageUsageRecord record=null;
		try{
			record=getById(toAccountRecordId);
			AccountingPersistence persistence = AccountingPersistenceFactory.getPersistence();
			persistence.account(record);
		}catch(Throwable e) {
			log.warn("Unable to account record {}.",record,e);
		}
		try{
			records.remove(toAccountRecordId);
		}catch(Throwable t) {
			log.warn("Unable to remove record by id {} ",toAccountRecordId,t);
		}
	}



	@Override
	public void setSuccessful(String id, boolean succesfull) {
		try {
			getById(id).setOperationResult(succesfull?OperationResult.SUCCESS:OperationResult.FAILED);
		} catch (Throwable e) {
			log.warn("Unable to update record {}.",getById(id),e);
		}
	}


	@Override
	public void setRead(String id) {
		try {
			getById(id).setOperationType(OperationType.READ);
		} catch (Throwable e) {
			log.warn("Unable to update record {}.",getById(id),e);
		}
	}


	@Override
	public void setCreate(String id) {
		try {
			getById(id).setOperationType(OperationType.CREATE);
		} catch (Throwable e) {
			log.warn("Unable to update record {}.",getById(id),e);
		}	
	}


	@Override
	public void setDelete(String id) {
		try {
			getById(id).setOperationType(OperationType.DELETE);
		} catch (Throwable e) {
			log.warn("Unable to update record {}.",getById(id),e);
		}
	}


	@Override
	public void setUpdate(String id) {
		try {
			getById(id).setOperationType(OperationType.UPDATE);
		} catch (Throwable e) {
			log.warn("Unable to update record {}.",getById(id),e);
		}
	}
	
	
	@Override
	public void setResourceURI(String id, String uri) {
		try {
			getById(id).setResourceURI(new URI(uri));
		} catch (Throwable e) {
			log.warn("Unable to update record {}.",getById(id),e);
		}
	}


	@Override
	public void setVolumne(String id, long volume) {
		try {
			getById(id).setDataVolume(volume);
		} catch (InvalidValueException e) {
			log.warn("Unable to update record {}.",getById(id),e);
		}
	}


	@Override
	public void setMimeType(String id,String mimeType) {
		try {
			getById(id).setQualifier(mimeType);
		} catch (InvalidValueException e) {
			log.warn("Unable to update record {}.",getById(id),e);
		}
	}

	
	
	
	
	private StorageUsageRecord initRecord(){
		StorageUsageRecord record=new StorageUsageRecord();
		try{
			String currentUser=TokenUtils.getCurrentUser();
		record.setConsumerId(currentUser);
		record.setResourceOwner(currentUser);
		record.setResourceScope(record.getScope());
		ApplicationContext context=ContextProvider.get();		
		ContainerConfiguration configuration=context.container().configuration();

		String hostName=configuration.hostname();		


		record.setProviderURI(new URI(hostName));
		record.setDataType(AbstractStorageUsageRecord.DataType.OTHER);
		}catch(Throwable t) {
			log.warn("Unable to create account record, returning empty one : {} ",record,t);
		} 


		return record;

		//		usageRecord.setOperationResult(TEST_OPERATION_RESULT);
		//		usageRecord.setResourceURI(new URI(TEST_RESOURCE_URI));
		//		usageRecord.setOperationType(AbstractStorageUsageRecord.OperationType.READ);
		//		usageRecord.setDataVolume(generateRandomLong(MIN_DATA_VOLUME, MAX_DATA_VOLUME));
		//		usageRecord.setQualifier("image/png");



	}



}
