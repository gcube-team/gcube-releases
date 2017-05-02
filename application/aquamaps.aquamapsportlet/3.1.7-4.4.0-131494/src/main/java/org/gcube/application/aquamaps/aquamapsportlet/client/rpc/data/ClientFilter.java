package org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.ClientFilterType;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ClientFilter implements IsSerializable{

	
	ClientFilterType type;
	ClientField field;
	
	public ClientFilterType getType() {
		return type;
	}
	
	public void setType(ClientFilterType type) {
		this.type = type;
	}

	public void setField(ClientField field) {
		this.field = field;
	}

	public ClientField getField() {
		return field;
	}

	
}
