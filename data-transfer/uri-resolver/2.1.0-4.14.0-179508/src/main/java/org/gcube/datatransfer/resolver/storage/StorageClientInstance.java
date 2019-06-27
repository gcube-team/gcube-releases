package org.gcube.datatransfer.resolver.storage;

import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;

/**
 * The Class StorageClientInstance.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Apr 2, 2019
 */
public class StorageClientInstance {
	
	
	StorageClient storageClient;
	MemoryType memory;
	String storageId;
	
	/**
	 * Instantiates a new storage client instance.
	 */
	public StorageClientInstance() {
	}
	
	

	/**
	 * Instantiates a new storage client instance.
	 *
	 * @param storageClient the storage client
	 * @param memory the memory
	 * @param storageId the storage id
	 */
	public StorageClientInstance(StorageClient storageClient, MemoryType memory, String storageId) {
		super();
		this.storageClient = storageClient;
		this.memory = memory;
		this.storageId = storageId;
	}



	/**
	 * Gets the storage client.
	 *
	 * @return the storage client
	 */
	public StorageClient getStorageClient() {
		return storageClient;
	}

	/**
	 * Sets the storage client.
	 *
	 * @param storageClient the new storage client
	 */
	public void setStorageClient(StorageClient storageClient) {
		this.storageClient = storageClient;
	}

	/**
	 * Gets the memory.
	 *
	 * @return the memory
	 */
	public MemoryType getMemory() {
		return memory;
	}

	/**
	 * Sets the memory.
	 *
	 * @param memory the new memory
	 */
	public void setMemory(MemoryType memory) {
		this.memory = memory;
	}

	/**
	 * Gets the storage id.
	 *
	 * @return the storage id
	 */
	public String getStorageId() {
		return storageId;
	}

	/**
	 * Sets the storage id.
	 *
	 * @param storageId the new storage id
	 */
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StorageClientInstance [storageClient=");
		builder.append(storageClient);
		builder.append(", memory=");
		builder.append(memory);
		builder.append(", storageId=");
		builder.append(storageId);
		builder.append("]");
		return builder.toString();
	}
	
	

}
