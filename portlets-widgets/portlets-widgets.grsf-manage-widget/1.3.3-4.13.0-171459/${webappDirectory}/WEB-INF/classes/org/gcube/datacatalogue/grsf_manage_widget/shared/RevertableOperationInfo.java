package org.gcube.datacatalogue.grsf_manage_widget.shared;

import java.io.Serializable;

public class RevertableOperationInfo implements Serializable{

	private static final long serialVersionUID = 5274434342849474800L;
	private String recordUrl;
	private String fullNameCurrentAdmin; // the one who is thinking to revert it
	private String userNameCurrentAdmin;
	private String uuid;
	private String fullNameOriginalAdmin; // the original admin in the link (his/her Full Name)
	private String userNameOriginalAdmin; // the original admin's username
	private long timestamp;
	private RevertableOperations operation;

	public RevertableOperationInfo() {
		super();
	}
	public RevertableOperationInfo(
			String recordUrl, 
			String fullNameCurrentAdmin,
			String userNameCurrentAdmin,
			String uuid, 
			String fullNameOriginalAdmin, 
			String userNameOriginalAdmin, 
			long timestamp, 
			RevertableOperations operation) {
		super();
		this.recordUrl = recordUrl;
		this.fullNameCurrentAdmin = fullNameCurrentAdmin;
		this.userNameCurrentAdmin = userNameCurrentAdmin;
		this.uuid = uuid;
		this.fullNameOriginalAdmin = fullNameOriginalAdmin;
		this.userNameOriginalAdmin = userNameOriginalAdmin;
		this.timestamp = timestamp;
		this.operation = operation;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public RevertableOperations getOperation() {
		return operation;
	}

	public void setOperation(RevertableOperations operation) {
		this.operation = operation;
	}

	public String getRecordUrl() {
		return recordUrl;
	}
	public void setRecordUrl(String recordUrl) {
		this.recordUrl = recordUrl;
	}

	public String getFullNameCurrentAdmin() {
		return fullNameCurrentAdmin;
	}

	public void setFullNameCurrentAdmin(String fullNameCurrentAdmin) {
		this.fullNameCurrentAdmin = fullNameCurrentAdmin;
	}

	public String getFullNameOriginalAdmin() {
		return fullNameOriginalAdmin;
	}

	public void setFullNameOriginalAdmin(String fullNameOriginalAdmin) {
		this.fullNameOriginalAdmin = fullNameOriginalAdmin;
	}

	public String getUserNameOriginalAdmin() {
		return userNameOriginalAdmin;
	}

	public void setUserNameOriginalAdmin(String userNameOriginalAdmin) {
		this.userNameOriginalAdmin = userNameOriginalAdmin;
	}

	public String getUserNameCurrentAdmin() {
		return userNameCurrentAdmin;
	}
	public void setUserNameCurrentAdmin(String userNameCurrentAdmin) {
		this.userNameCurrentAdmin = userNameCurrentAdmin;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@Override
	public String toString() {
		return "RevertableOperationInfo [recordUrl=" + recordUrl
				+ ", fullNameCurrentAdmin=" + fullNameCurrentAdmin
				+ ", userNameCurrentAdmin=" + userNameCurrentAdmin + ", uuid="
				+ uuid + ", fullNameOriginalAdmin=" + fullNameOriginalAdmin
				+ ", userNameOriginalAdmin=" + userNameOriginalAdmin
				+ ", timestamp=" + timestamp + ", operation=" + operation + "]";
	}
}
