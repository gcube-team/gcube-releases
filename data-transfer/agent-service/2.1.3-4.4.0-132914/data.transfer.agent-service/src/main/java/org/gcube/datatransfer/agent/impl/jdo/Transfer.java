package org.gcube.datatransfer.agent.impl.jdo;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Transfer implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	private String id ="";
	//Can be the user DN if available or the service caller (serviceclass/servicename)
	protected String submitter;
	
	protected String submitterEndpoint; //(schedulerEndpoint) if messaging is enabled
	protected boolean lastNotificationMsgSent; //if messaging is enabled
	protected String status;
	
	protected long totalTransfers;
	protected long transfersCompleted;
	
	protected long totalSize;
	
	protected long sizeTransferred;
	
	protected String sourceID; //for Tree Transfer
	protected String destID;  //for Tree Transfer
	protected String outcome;  //for Tree Transfer
	protected int totalReadTrees;  //trees been read for the transfer
	protected int totalWrittenTrees;  //successfully transferred trees
	
	protected int updates=0; //in case of a periodical transfer
	

	public int getUpdates() {
		return updates;
	}

	public void setUpdates(int updates) {
		this.updates = updates;
	}

	public long getTotaltransfers() {
		return totalTransfers;
	}

	public void setTotalTransfers(long totaltransfers) {
		this.totalTransfers = totaltransfers;
	}

	public long getTransfersCompleted() {
		return transfersCompleted;
	}

	public void setTransfersCompleted(long transfersCompleted) {
		this.transfersCompleted = transfersCompleted;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public long getSizeTransferred() {
		return sizeTransferred;
	}

	public void setSizeTransferred(Long sizeTransferred) {
		this.sizeTransferred = sizeTransferred;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String transferId) {
		this.id = transferId;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubmitterEndpoint() {
		return submitterEndpoint;
	}

	public void setSubmitterEndpoint(String submitterEndpoint) {
		this.submitterEndpoint = submitterEndpoint;
	}

	public boolean isLastNotificationMsgSent() {
		return lastNotificationMsgSent;
	}

	public void setLastNotificationMsgSent(boolean lastNotificationMsgSent) {
		this.lastNotificationMsgSent = lastNotificationMsgSent;
	}

	public long getTotalTransfers() {
		return totalTransfers;
	}

	public String getSourceID() {
		return sourceID;
	}

	public String getDestID() {
		return destID;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setSizeTransferred(long sizeTransferred) {
		this.sizeTransferred = sizeTransferred;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public void setDestID(String destID) {
		this.destID = destID;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public int getTotalReadTrees() {
		return totalReadTrees;
	}

	public int getTotalWrittenTrees() {
		return totalWrittenTrees;
	}

	public void setTotalReadTrees(int totalReadTrees) {
		this.totalReadTrees = totalReadTrees;
	}

	public void setTotalWrittenTrees(int totalWrittenTrees) {
		this.totalWrittenTrees = totalWrittenTrees;
	}
	
}
