package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum ClientFilterOperator implements IsSerializable{

	begins,contains,is,ends,greater_then,smaller_then;
	private ClientFilterOperator() {
		// TODO Auto-generated constructor stub
	}
	
}
