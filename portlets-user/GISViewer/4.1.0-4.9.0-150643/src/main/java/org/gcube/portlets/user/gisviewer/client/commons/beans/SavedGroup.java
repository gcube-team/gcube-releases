package org.gcube.portlets.user.gisviewer.client.commons.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SavedGroup implements IsSerializable{
	private String name;
	private String url;
	public SavedGroup() {
		super();
	}
	public SavedGroup(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
