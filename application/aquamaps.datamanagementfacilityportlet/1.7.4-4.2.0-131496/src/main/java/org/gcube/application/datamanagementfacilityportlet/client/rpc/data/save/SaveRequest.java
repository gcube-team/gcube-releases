package org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SaveRequest implements IsSerializable {

	private SaveOperationType type=SaveOperationType.RESOURCE;
	private String toSaveId;
	private String destinationBasketId;
	private String toSaveName;
	
	public SaveRequest() {
		// TODO Auto-generated constructor stub
	}

	public SaveRequest(SaveOperationType type, String toSaveId,
			String destinationBasketId, String toSaveName) {
		super();
		this.type = type;
		this.toSaveId = toSaveId;
		this.destinationBasketId = destinationBasketId;
		this.toSaveName = toSaveName;
	}

	public SaveOperationType getType() {
		return type;
	}

	public void setType(SaveOperationType type) {
		this.type = type;
	}

	public String getToSaveId() {
		return toSaveId;
	}

	public void setToSaveId(String toSaveId) {
		this.toSaveId = toSaveId;
	}

	public String getDestinationBasketId() {
		return destinationBasketId;
	}

	public void setDestinationBasketId(String destinationBasketId) {
		this.destinationBasketId = destinationBasketId;
	}

	public String getToSaveName() {
		return toSaveName;
	}

	public void setToSaveName(String toSaveName) {
		this.toSaveName = toSaveName;
	}

	@Override
	public String toString() {
		return "SaveRequest [type=" + type + ", toSaveId=" + toSaveId
				+ ", destinationBasketId=" + destinationBasketId
				+ ", toSaveName=" + toSaveName + "]";
	}
	
}
