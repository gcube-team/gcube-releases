package org.gcube.datatransfer.scheduler.library.outcome;

import java.util.List;

import org.gcube.datatransfer.scheduler.library.obj.TypeOfSchedule;

public class TransferInfo {
	
	private String transferId;
	
	protected String submitter;
	protected String status;
	public int numOdUpdates;	
	
	//if Completed
	private String[] objectTrasferredIDs;
	private String[] objectFailedIDs;
	
	//if failed 
	public List<String> transferError;
	public String transferIdOfAgent;	
		
	//type of schedule
	protected TypeOfSchedule typeOfSchedule;
	//"dd.MM.yy-hh.mm"
	String submittedDate; 
	
	//showing progress
	public long total_size;
	public long bytes_have_been_transferred;
	
	public TransferInfo(){
		this.transferId = null;
		this.submitter = null;
		this.status = null;
		this.objectTrasferredIDs = null;
		this.objectFailedIDs = null;
		this.transferError = null;
		this.transferIdOfAgent = null;
		this.typeOfSchedule = new TypeOfSchedule();
		this.submittedDate=null;
		this.total_size=0;
		this.bytes_have_been_transferred=0;	
		this.numOdUpdates=0;
	}

	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
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

	public String[] getObjectTrasferredIDs() {
		return objectTrasferredIDs;
	}

	public void setObjectTrasferredIDs(String[] objectTrasferredIDs) {
		this.objectTrasferredIDs = objectTrasferredIDs;
	}

	public String[] getObjectFailedIDs() {
		return objectFailedIDs;
	}

	public void setObjectFailedIDs(String[] objectFailedIDs) {
		this.objectFailedIDs = objectFailedIDs;
	}

	public List<String> getTransferError() {
		return transferError;
	}

	public void setTransferError(List<String> transferError) {
		this.transferError = transferError;
	}

	public String getTransferIdOfAgent() {
		return transferIdOfAgent;
	}

	public void setTransferIdOfAgent(String transferIdOfAgent) {
		this.transferIdOfAgent = transferIdOfAgent;
	}

	public TypeOfSchedule getTypeOfSchedule() {
		return typeOfSchedule;
	}

	public void setTypeOfSchedule(TypeOfSchedule typeOfSchedule) {
		this.typeOfSchedule = typeOfSchedule;
	}

	public String getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}

	public long getTotal_size() {
		return total_size;
	}

	public long getBytes_have_been_transferred() {
		return bytes_have_been_transferred;
	}

	public void setTotal_size(long total_size) {
		this.total_size = total_size;
	}

	public void setBytes_have_been_transferred(long bytes_have_been_transferred) {
		this.bytes_have_been_transferred = bytes_have_been_transferred;
	}

	public int getNumOdUpdates() {
		return numOdUpdates;
	}

	public void setNumOdUpdates(int numOdUpdates) {
		this.numOdUpdates = numOdUpdates;
	}
	

	
	
}
