package org.gcube.datatransfer.agent.impl.jdo;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;


@PersistenceCapable
public class TransferObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
    private String id;  
	private String sourceURI;
	private String destURI;
	private Long size;
	private String status;
	private String outcome;
	private TransferType transferType;
	private String transferID;
	private Long transferTime;
	private Long bytesOfObjTransferred;
	
	public Long getTransferTime() {
		return transferTime;
	}
	public void setTransferTime(Long transferTime) {
		this.transferTime = transferTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}
	public void setId(String objectId) {
		this.id = objectId;
	}
	
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public String getSourceURI() {
		return sourceURI;
	}
	public void setSourceURI(String sourceURI) {
		this.sourceURI = sourceURI;
	}
	public String getDestURI() {
		return destURI;
	}
	public void setDestURI(String destURI) {
		this.destURI = destURI;
	}
	public String getOutcome() {
		return outcome;
	}
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	public TransferType getTransferType() {
		return transferType;
	}
	public void setTransferType(TransferType transferType) {
		this.transferType = transferType;
	}
	public String getTransferID() {
		return transferID;
	}
	public void setTransferID(String transferID) {
		this.transferID = transferID;
	}
	public Long getBytesOfObjTransferred() {
		return bytesOfObjTransferred;
	}
	public void setBytesOfObjTransferred(Long bytesOfObjTransferred) {
		this.bytesOfObjTransferred = bytesOfObjTransferred;
	}	

}
