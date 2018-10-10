package org.gcube.data_catalogue.grsf_publish_ws.json.input.others;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.common.enums.Status;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateRecordStatus {

	@JsonProperty(Constants.KB_ID)
	@NotNull(message= Constants.KB_ID + " cannot be null")
	@Size(min=1, message= Constants.KB_ID + " cannot be empty")
	private String uuid;

	@JsonProperty(Constants.NEW_STATUS)
	private Status newStatus;

	public UpdateRecordStatus() {
		super();
	}

	/**
	 * @param uuid
	 * @param newStatus
	 */
	public UpdateRecordStatus(String uuid, Status newStatus) {
		super();
		this.uuid = uuid;
		this.newStatus = newStatus;
	}

	public Status getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(Status newStatus) {
		this.newStatus = newStatus;
	}


	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "UpdateRecordStatus [uuid=" + uuid + ", newStatus=" + newStatus
				+ "]";
	}

}
