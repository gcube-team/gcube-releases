package org.gcube.datatransfer.scheduler.db.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;




@PersistenceCapable(table="DATASTORAGE_QUOTA")
public class DataStorageQuota implements java.io.Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3581645833835665656L;
	@PrimaryKey
	private String storageId;
	
	public String getStorageId() {
		return storageId;
	}
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}
	
}
