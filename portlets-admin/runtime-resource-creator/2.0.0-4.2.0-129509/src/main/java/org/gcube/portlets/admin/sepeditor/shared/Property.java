package org.gcube.portlets.admin.sepeditor.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Property implements Serializable{
	private String key;
	private String value;
	private boolean isCrypted;
	
	public Property() {
		super();
	}

	public Property(String key, String value, boolean isCrypted) {
		super();
		this.key = key;
		this.value = value;
		this.isCrypted = isCrypted;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isCrypted() {
		return isCrypted;
	}

	public void setCrypted(boolean isCrypted) {
		this.isCrypted = isCrypted;
	}
	
	
}
