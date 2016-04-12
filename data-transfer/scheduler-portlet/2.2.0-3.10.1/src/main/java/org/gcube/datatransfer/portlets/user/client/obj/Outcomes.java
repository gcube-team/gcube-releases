package org.gcube.datatransfer.portlets.user.client.obj;

public class Outcomes {
	String id;

	String fileName;
	String destination;
	String success;
	String failure;
	String transferTime;
	String size;
	String transferredBytes;
	String exception;
	String totalMessage;
	
	public Outcomes(){
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getFailure() {
		return failure;
	}

	public void setFailure(String failure) {
		this.failure = failure;
	}

	public String getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getTotalMessage() {
		return totalMessage;
	}

	public void setTotalMessage(String totalMessage) {
		this.totalMessage = totalMessage;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTransferredBytes() {
		return transferredBytes;
	}

	public void setTransferredBytes(String transferredBytes) {
		this.transferredBytes = transferredBytes;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	
}
