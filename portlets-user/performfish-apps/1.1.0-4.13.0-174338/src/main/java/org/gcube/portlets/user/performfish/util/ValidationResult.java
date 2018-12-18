package org.gcube.portlets.user.performfish.util;

public class ValidationResult {
	boolean success;
	String comment;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public ValidationResult(boolean success, String comment) {
		super();
		this.success = success;
		this.comment = comment;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ValidationResult [success=");
		builder.append(success);
		builder.append(", comment=");
		builder.append(comment);
		builder.append("]");
		return builder.toString();
	}
	

}
