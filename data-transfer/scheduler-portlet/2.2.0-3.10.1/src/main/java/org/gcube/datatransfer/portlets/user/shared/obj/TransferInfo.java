package org.gcube.datatransfer.portlets.user.shared.obj;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kfuntak.gwt.json.serialization.client.JsonSerializable;


public class TransferInfo implements JsonSerializable{
	//type of schedule info
	public String typeOfScheduleString;

	public String transferId;
	
	public String submitter;
	public String status;
	//"dd.MM.yy-HH.mm.ss"
	public String submittedDate; 
	public Date submittedDate2;
	
	//if Completed
	public String[] objectTrasferredIDs;
	public String[] objectFailedIDs;
	
	//if failed 
	public List<String> transferError;
	public String transferIdOfAgent;	
	
	//showing progress
	public long total_size;
	public long bytes_have_been_transferred;
	public double progress;
	
	public int numOfUpdates;



	public TransferInfo(){
		this.typeOfScheduleString="";
		this.transferId = "";
		this.submitter = "";
		this.status = "";
		this.objectTrasferredIDs = new String[]{};
		this.objectFailedIDs = new String[]{};
		this.transferError = new ArrayList<String>();
		this.transferError.add("");
		this.transferIdOfAgent = "";
		this.submittedDate="";
		this.submittedDate2=null;
		this.progress=0;
		this.total_size=0;
		this.bytes_have_been_transferred=0;
		this.numOfUpdates=0;
	}
	
	public double calculateProgress(){
		if(total_size!=0){
			this.progress= (double)this.bytes_have_been_transferred/(double)this.total_size;
		}
		else this.progress=0;
		return progress;
	}
	public String getTypeOfScheduleString() {
		return typeOfScheduleString;
	}

	public void setTypeOfScheduleString(String typeOfScheduleString) {
		this.typeOfScheduleString = typeOfScheduleString;
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

	public int getNumOfUpdates() {
		return numOfUpdates;
	}

	public void setNumOfUpdates(int numOfUpdates) {
		this.numOfUpdates = numOfUpdates;
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
	public String getSubmittedDate() {
		return submittedDate;
	}
	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}
	public Date getSubmittedDate2() {
		return submittedDate2;
	}
	public void setSubmittedDate2(Date submittedDate2) {
		this.submittedDate2 = submittedDate2;
	}
	public double getProgress() {
		return progress;
	}
	public void setProgress(double progress) {
		this.progress = progress;
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
}
