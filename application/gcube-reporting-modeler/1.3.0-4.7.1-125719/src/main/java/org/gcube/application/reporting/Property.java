package org.gcube.application.reporting;

public class Property {
	
	private String key;
	
	private String value;

	public Property(String key, String value) {
		super();
		this.key = key;
		this.value = value;
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

	@Override
	public String toString() {
		return "Property [key=" + key + ", value=" + value + "]";
	}
	
	
}
