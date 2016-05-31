package org.gcube.portlets.user.searchportlet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BrowsableFieldBean implements IsSerializable{

	private String id;
	
	private String name;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private BrowsableFieldBean() {}
	
	public BrowsableFieldBean(String id, String name) {
		this.id = id;
		this.name = name;
	}
}
