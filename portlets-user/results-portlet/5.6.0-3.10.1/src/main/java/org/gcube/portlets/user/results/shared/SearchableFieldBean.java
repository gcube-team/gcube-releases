package org.gcube.portlets.user.results.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchableFieldBean implements IsSerializable {
	
	private String name;
	
	private String value;
	
	private SearchableFieldBean() {}
	
	public SearchableFieldBean(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
