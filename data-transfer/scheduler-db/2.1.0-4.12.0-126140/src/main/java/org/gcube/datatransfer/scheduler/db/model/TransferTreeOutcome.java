package org.gcube.datatransfer.scheduler.db.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table="TRANSFER_TREE_OUTCOMES")
public class TransferTreeOutcome  implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7610055030912173099L;

	@PrimaryKey	
	String transferTreeOutcomesId;
	
	String submittedDateOfTransfer;
	String transferId;
	
	String sourceId;
	String storageId;
	String treeException;  //for Tree Transfer
	int totalReadTrees;  //trees been read for the transfer
	int totalWrittenTrees;  //successfully transferred trees
	boolean success;
	boolean failure;
	


	public TransferTreeOutcome() {
		this.submittedDateOfTransfer = null;
		this.transferId = null;
		this.sourceId = null;
		this.storageId = null;
		this.treeException = null;
		this.totalReadTrees = 0;
		this.totalWrittenTrees = 0;
		this.success = false;
		this.failure = false;
	}



	public String getTransferTreeOutcomesId() {
		return transferTreeOutcomesId;
	}



	public String getSubmittedDateOfTransfer() {
		return submittedDateOfTransfer;
	}



	public String getTransferId() {
		return transferId;
	}



	public String getSourceId() {
		return sourceId;
	}



	public String getStorageId() {
		return storageId;
	}



	public String getTreeException() {
		return treeException;
	}



	public int getTotalReadTrees() {
		return totalReadTrees;
	}



	public int getTotalWrittenTrees() {
		return totalWrittenTrees;
	}



	public boolean isSuccess() {
		return success;
	}



	public boolean isFailure() {
		return failure;
	}



	public void setTransferTreeOutcomesId(String transferTreeOutcomesId) {
		this.transferTreeOutcomesId = transferTreeOutcomesId;
	}



	public void setSubmittedDateOfTransfer(String submittedDateOfTransfer) {
		this.submittedDateOfTransfer = submittedDateOfTransfer;
	}



	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}




	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}



	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}



	public void setTreeException(String treeException) {
		this.treeException = treeException;
	}



	public void setTotalReadTrees(int totalReadTrees) {
		this.totalReadTrees = totalReadTrees;
	}



	public void setTotalWrittenTrees(int totalWrittenTrees) {
		this.totalWrittenTrees = totalWrittenTrees;
	}



	public void setSuccess(boolean success) {
		this.success = success;
	}



	public void setFailure(boolean failure) {
		this.failure = failure;
	}

	
}
