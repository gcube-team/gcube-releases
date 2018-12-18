package org.gcube.datatransfer.common.agent;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;



import org.w3c.dom.Element;

public class Types {

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class CreateTreeSourceMsg{

		@XmlElement(name="sourceID")
		public String sourceID;

		@XmlElement(name="endpoint")
		public String endpoint;

		@XmlElement(name="port")
		public int port;

		public String getSourceID() {
			return sourceID;
		}
		public String getEndpoint() {
			return endpoint;
		}
		public int getPort() {
			return port;
		}
		public void setSourceID(String sourceID) {
			this.sourceID = sourceID;
		}
		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}
		public void setPort(int port) {
			this.port = port;
		}
	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class StartTransferMessage{

		@XmlElement(name="Source")
		public SourceData source;

		@XmlElement(name="Dest")
		public DestData dest;

		@XmlElement
		public boolean syncOp;

		public SourceData getSource() {
			return source;
		}

		public void setSource(SourceData source) {
			this.source = source;
		}

		public DestData getDest() {
			return dest;
		}

		public void setDest(DestData dest) {
			this.dest = dest;
		}

		public boolean isSyncOp() {
			return syncOp;
		}

		public void setSyncOp(boolean syncOp) {
			this.syncOp = syncOp;
		}


	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	
	 public static class CancelTransferMessage{
		
		@XmlElement(name="TransferID")
		public String transferId;

		@XmlElement
		public boolean forceStop;

		public String getTransferId() {
			return transferId;
		}

		public void setTransferId(String transferId) {
			this.transferId = transferId;
		}

		public boolean isForceStop() {
			return forceStop;
		}

		public void setForceStop(boolean forceStop) {
			this.forceStop = forceStop;
		}
		 
		 
	 }
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	
	public static class MonitorTransferReportMessage implements Serializable{

		private static final long serialVersionUID = 3269711116186691039L;

		@XmlElement
		public String transferID;
		
		@XmlElement
		public int totalTransfers;
		
		@XmlElement
		public int transferCompleted;
		
		@XmlElement
		public long totalBytes;
		
		@XmlElement
		public long bytesTransferred;
		
		@XmlElement
		public String transferStatus;

		public String getTransferID() {
			return transferID;
		}

		public void setTransferID(String transferID) {
			this.transferID = transferID;
		}

		public int getTotalTransfers() {
			return totalTransfers;
		}

		public void setTotalTransfers(int totalTransfers) {
			this.totalTransfers = totalTransfers;
		}

		public int getTransferCompleted() {
			return transferCompleted;
		}

		public void setTransferCompleted(int transferCompleted) {
			this.transferCompleted = transferCompleted;
		}

		public long getTotalBytes() {
			return totalBytes;
		}

		public void setTotalBytes(long totalBytes) {
			this.totalBytes = totalBytes;
		}

		public long getBytesTransferred() {
			return bytesTransferred;
		}

		public void setBytesTransferred(long bytesTransferred) {
			this.bytesTransferred = bytesTransferred;
		}

		public String getTransferStatus() {
			return transferStatus;
		}

		public void setTransferStatus(String transferStatus) {
			this.transferStatus = transferStatus;
		}
		
	}

	@XmlEnum(String.class)
	public enum transferType implements Serializable{ TreeBasedTransfer, FileBasedTransfer, LocalFileBasedTransfer }

	@XmlEnum(String.class)
	public enum storageType implements Serializable{ LocalGHN, StorageManager, DataStorage }

	@XmlEnum(String.class)
	public enum storageAccessType implements Serializable{ SHARED, PUBLIC, PRIVATE }


	@XmlEnum(String.class)
	public enum postProcessType implements Serializable{ FileConversion, FileUnzip, OriginalFileRemove }

	@XmlAccessorType(XmlAccessType.FIELD)
	
	public static class StorageManagerDetails implements Serializable{

		private static final long serialVersionUID = 7115158097725009454L;
		@XmlElement
		public String serviceClass;
		@XmlElement
		public String serviceName;
		@XmlElement
		public String Owner;		
		@XmlElement
		public storageAccessType accessType;
		public String getServiceClass() {
			return serviceClass;
		}
		public void setServiceClass(String serviceClass) {
			this.serviceClass = serviceClass;
		}
		public String getServiceName() {
			return serviceName;
		}
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
		public String getOwner() {
			return Owner;
		}
		public void setOwner(String owner) {
			Owner = owner;
		}
		public storageAccessType getAccessType() {
			return accessType;
		}
		public void setAccessType(storageAccessType accessType) {
			this.accessType = accessType;
		}
		
	}
	
	
	@XmlAccessorType(XmlAccessType.FIELD)
	
	public static class TransferOptions implements Serializable{

		private static final long serialVersionUID = 8971009772278624772L;
		@XmlElement
		public boolean overwrite;
		@XmlElement
		public storageType storageType;
		@XmlElement
		public long transferTimeout;		
		@XmlElement
		public StorageManagerDetails storageManagerDetails;
		@XmlElement
		public List<postProcessType> postProcess;	
	
		@XmlElement
		public String  conversionType;

		public boolean isOverwrite() {
			return overwrite;
		}

		public void setOverwrite(boolean overwrite) {
			this.overwrite = overwrite;
		}

		public storageType getStorageType() {
			return storageType;
		}

		public void setStorageType(storageType storageType) {
			this.storageType = storageType;
		}

		public long getTransferTimeout() {
			return transferTimeout;
		}

		public void setTransferTimeout(long transferTimeout) {
			this.transferTimeout = transferTimeout;
		}

		public StorageManagerDetails getStorageManagerDetails() {
			return storageManagerDetails;
		}

		public void setStorageManagerDetails(StorageManagerDetails storageManagerDetails) {
			this.storageManagerDetails = storageManagerDetails;
		}

		public List<postProcessType> getPostProcess() {
			return postProcess;
		}

		public void setPostProcess(List<postProcessType> postProcess) {
			this.postProcess = postProcess;
		}

		public String getConversionType() {
			return conversionType;
		}

		public void setConversionType(String conversionType) {
			this.conversionType = conversionType;
		}		
	
		
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	
	public static  class DestData {
		@XmlElement
		public transferType type;

		@XmlElement
		public String scope;
		
		@XmlElement
		public  OutUriData outUri;
		
		@XmlElement
		public String outSourceId;

		public transferType getType() {
			return type;
		}

		public void setType(transferType type) {
			this.type = type;
		}

		public String getScope() {
			return scope;
		}

		public void setScope(String scope) {
			this.scope = scope;
		}

		public OutUriData getOutUri() {
			return outUri;
		}

		public void setOutUri(OutUriData outUri) {
			this.outUri = outUri;
		}

		public String getOutSourceId() {
			return outSourceId;
		}

		public void setOutSourceId(String outSourceId) {
			this.outSourceId = outSourceId;
		}	

	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	
	public static class OutUriData  {

		@XmlElement
		public List<String> OutUris;	
		@XmlElement
		public TransferOptions options;
		public List<String> getOutUris() {
			return OutUris;
		}
		public void setOutUris(List<String> outUris) {
			OutUris = outUris;
		}
		public TransferOptions getOptions() {
			return options;
		}
		public void setOptions(TransferOptions options) {
			this.options = options;
		}
	}
	
	

	@XmlAccessorType(XmlAccessType.FIELD)
	
	public static  class SourceData {
		@XmlElement
		public transferType type;

		@XmlElement
		public String scope;
		
		@XmlElement
		public InputPattern inputSource;
		
		@XmlElement
		public List<String> inputURIs;

		public transferType getType() {
			return type;
		}

		public void setType(transferType type) {
			this.type = type;
		}

		public String getScope() {
			return scope;
		}

		public void setScope(String scope) {
			this.scope = scope;
		}

		public InputPattern getInputSource() {
			return inputSource;
		}

		public void setInputSource(InputPattern inputSource) {
			this.inputSource = inputSource;
		}

		public List<String> getInputURIs() {
			return inputURIs;
		}

		public void setInputURIs(List<String> inputURIs) {
			this.inputURIs = inputURIs;
		}

	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	
	public static class InputPattern  {

		@XmlElement
		public AnyHolder pattern;
		@XmlElement
		public String sourceId;
		public AnyHolder getPattern() {
			return pattern;
		}
		public void setPattern(AnyHolder pattern) {
			this.pattern = pattern;
		}
		public String getSourceId() {
			return sourceId;
		}
		public void setSourceId(String sourceId) {
			this.sourceId = sourceId;
		}
	}
	
	public static class InputURIs {
		@XmlElement(name="inputURIs")
		public List<String> uris;
	}
	public static class AnyHolder {
		
		@XmlAnyElement
		public Element [] element;

	}

}
