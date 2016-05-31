package org.gcube.portlets.user.results.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GenericTreeRecordBean implements IsSerializable {

	private ObjectType type;
	private String payload;
	
	public GenericTreeRecordBean(){}
	
	public GenericTreeRecordBean(ObjectType type, String payload) {
		this.type = type;
		this.payload = payload;
	}

	public ObjectType getType() {
		return type;
	}

	public String getPayload() {
		return payload;
	};
	
}
