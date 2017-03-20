package org.gcube.datatransfer.scheduler.db.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="TRANSFER_OUTCOMES")
public class TransferOutcome  implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7610055030912173099L;

	@PrimaryKey	
	String transferOutcomesId;
	
	String submittedDateOfTransfer;
	String transferId;
	int numberOfOutcomeInThisTransfer;
	
	String fileName;
	String exception;
	String dest;
	String transferTime;
	String transferredBytesOfObj;
	String size;
	boolean success;
	boolean failure;
	
	public TransferOutcome() {
		this.transferId=null;
		this.submittedDateOfTransfer = null;
		this.exception = null;
		this.fileName = null;
		this.dest = null;
		this.transferTime = null;
		this.success = false;
		this.failure = false;
		this.numberOfOutcomeInThisTransfer=0;
		this.transferredBytesOfObj=null;
		this.size=null;
	}

	public int getNumberOfOutcomeInThisTransfer() {
		return numberOfOutcomeInThisTransfer;
	}

	public void setNumberOfOutcomeInThisTransfer(int numberOfOutcomeInThisTransfer) {
		this.numberOfOutcomeInThisTransfer = numberOfOutcomeInThisTransfer;
	}

	public String getTransferOutcomesId() {
		return transferOutcomesId;
	}

	public void setTransferOutcomesId(String transferOutcomesId) {
		this.transferOutcomesId = transferOutcomesId;
	}

	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}



	public String getSubmittedDateOfTransfer() {
		return submittedDateOfTransfer;
	}

	public void setSubmittedDateOfTransfer(String submittedDateOfTransfer) {
		this.submittedDateOfTransfer = submittedDateOfTransfer;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isFailure() {
		return failure;
	}

	public void setFailure(boolean failure) {
		this.failure = failure;
	}

	public String getTransferredBytesOfObj() {
		return transferredBytesOfObj;
	}

	public void setTransferredBytesOfObj(String transferredBytesOfObj) {
		this.transferredBytesOfObj = transferredBytesOfObj;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	
	
}
