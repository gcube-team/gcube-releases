package org.gcube.portlets.admin.searchmanagerportlet.gwt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CapabilityBean implements IsSerializable{

	private String name;

	private String ID;

	private CapabilityBean() {}

	public CapabilityBean(String name, String id) {
		this.name = name;
		this.ID = id;
	}

	public String getName() {
		return name;
	}

	public String getID() {
		return ID;
	}

	public void setName(String name) {
		this.name = name;
	}
}

