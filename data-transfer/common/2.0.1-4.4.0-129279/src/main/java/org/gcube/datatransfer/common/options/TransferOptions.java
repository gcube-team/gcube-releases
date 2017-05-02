package org.gcube.datatransfer.common.options;


import java.util.concurrent.TimeUnit;

import org.gcube.datatransfer.common.agent.Types.StorageManagerDetails;
import org.gcube.datatransfer.common.agent.Types.storageType;

/**
 * 
 * @author andrea
 *
 */
public class TransferOptions{
	
	storageType type;
	boolean overwriteFile;
	boolean unzipFile;
	boolean covertFile;
	boolean deleteOriginalFile;
	long transferTimeout = 3600000;


	ConversionType conversionType;
	
	StorageManagerDetails storageManagerDetails;
	
	
	public ConversionType getConversionType() {
		return conversionType;
	}
	public void setConversionType(ConversionType conversionType) {
		this.conversionType = conversionType;
	}
	public boolean isCovertFile() {
		return covertFile;
	}
	public void setCovertFile(boolean covertFile) {
		this.covertFile = covertFile;
	}
	public boolean isOverwriteFile() {
		return overwriteFile;
	}
	public void setOverwriteFile(boolean overwriteFile) {
		this.overwriteFile = overwriteFile;
	}
	public boolean isUnzipFile() {
		return unzipFile;
	}
	public void setUnzipFile(boolean unzipFile) {
		this.unzipFile = unzipFile;
	}
	
	
	public storageType getType() {
		return type;
	}
	public void setType(storageType type) {
		this.type = type;
	}

	public StorageManagerDetails getStorageManagerDetails() {
		return storageManagerDetails;
	}
	public void setStorageManagerDetails(StorageManagerDetails storageManagerDetails) {
		this.storageManagerDetails = storageManagerDetails;
	}
	
	public boolean isDeleteOriginalFile() {
		return deleteOriginalFile;
	}
	public void setDeleteOriginalFile(boolean deleteOriginalFile) {
		this.deleteOriginalFile = deleteOriginalFile;
	}
	
	public enum ConversionType {
		GEOTIFF("GEOTIFF");
		String type;
		ConversionType(String type){this.type = type;}
		public String toString(){return this.type;}
	}

	public long getTransferTimeout() {
		return transferTimeout;
	}
	public void setTransferTimeout(long transferTimeout, TimeUnit unit) {
		this.transferTimeout = unit.toMillis(transferTimeout);
	}

}
