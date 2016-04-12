package org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data;

import org.gcube.application.aquamaps.aquamapsportlet.client.constants.types.AreaType;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ClientArea implements IsSerializable{

	private AreaType type;
	private String code;
	private String name;

	public ClientArea() {
	}
	
	public ClientArea(AreaType type,String code,String name) {
		this.code=code;
		this.type=type;
		this.name=name;
	}

	public AreaType getType() {
		return type;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
