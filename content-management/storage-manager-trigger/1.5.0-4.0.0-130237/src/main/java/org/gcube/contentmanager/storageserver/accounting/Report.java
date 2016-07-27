package org.gcube.contentmanager.storageserver.accounting;


import org.gcube.accounting.datamodel.usagerecords.StorageUsageRecord;

public interface Report {	
	
	public void init();
	/**
	 * Set generic properties of report
	 * @param resourceType
	 * @param consumerId
	 * @param resourceOwner
	 * @param resourceScope
	 * @return
	 */
	public StorageUsageRecord setGenericProperties(StorageUsageRecord sur, String resourceType, String consumerId, String resourceScope, String creationTime, String lastAccess, String owner, String operation, String size);
	
	/**
	 * Set end time of operation and other specific properties
	 * @return
	 */
	public StorageUsageRecord setSpecificProperties(StorageUsageRecord sur, String filePath,  String dataType, String callerIP, String id);
	
	/**
	 * send report
	 * @return
	 */
	public void send(StorageUsageRecord sur);
	
	/**
	 * 
	 */

	public abstract void printRecord(StorageUsageRecord record);
}
