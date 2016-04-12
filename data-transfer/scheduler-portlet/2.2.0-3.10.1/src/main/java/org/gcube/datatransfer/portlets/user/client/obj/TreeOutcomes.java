package org.gcube.datatransfer.portlets.user.client.obj;

public class TreeOutcomes {
	public String id;
	
	public String sourceID;
	public String destID;
	public String success;
	public String failure;
	public String readTrees;
	public String writtenTrees;
	public String exception;
	public String totalMessage;

	
	public TreeOutcomes(){
	}

	public String getSuccess() {
		return success;
	}

	public String getFailure() {
		return failure;
	}

	public String getTotalMessage() {
		return totalMessage;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public void setFailure(String failure) {
		this.failure = failure;
	}

	public void setTotalMessage(String totalMessage) {
		this.totalMessage = totalMessage;
	}

	public String getSourceID() {
		return sourceID;
	}

	public String getDestID() {
		return destID;
	}

	public String getReadTrees() {
		return readTrees;
	}

	public String getWrittenTrees() {
		return writtenTrees;
	}

	public String getException() {
		return exception;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public void setDestID(String destID) {
		this.destID = destID;
	}

	public void setReadTrees(String readTrees) {
		this.readTrees = readTrees;
	}

	public void setWrittenTrees(String writtenTrees) {
		this.writtenTrees = writtenTrees;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
	
}
