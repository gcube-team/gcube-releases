package org.gcube.portlets.user.newsfeed.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class OperationResult implements Serializable {
	
	private Boolean success;
	private String message;
	private Serializable object;
	
	public OperationResult() {
		super();
	}

	public OperationResult(Boolean success, String message, Serializable object) {
		super();
		this.success = success;
		this.message = message;
		this.object = object;
	}

	public Boolean isSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Serializable getObject() {
		return object;
	}

	public void setObject(Serializable object) {
		this.object = object;
	}
	
	
}
