package org.gcube.portlets.user.newsfeed.shared;

import org.gcube.portal.databook.shared.Comment;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OperationResult implements IsSerializable {
	
	private Boolean success;
	private String message;
	private Comment comment;
	
	public OperationResult() {
		super();
	}

	public OperationResult(Boolean success, String message, Comment comment) {
		super();
		this.success = success;
		this.message = message;
		this.comment = comment;
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

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}	
}
