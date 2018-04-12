package org.gcube.datapublishing.sdmx.impl.reports;

import java.util.ArrayList;
import java.util.List;

public class SubmissionReport {

	String id;

	OperationStatus status;

	List<String> messages = new ArrayList<String>();
	
	public SubmissionReport() {
	}
	
	public SubmissionReport(String id, OperationStatus status, List<String> messages) {
		super();
		this.id = id;
		this.status = status;
		this.messages = messages;
	}	
	
	public void addMessage(String message){
		messages.add(message);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OperationStatus getStatus() {
		return status;
	}

	public void setStatus(OperationStatus status) {
		this.status = status;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	

}
