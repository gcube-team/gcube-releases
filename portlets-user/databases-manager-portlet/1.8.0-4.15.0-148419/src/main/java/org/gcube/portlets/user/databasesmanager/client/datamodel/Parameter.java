package org.gcube.portlets.user.databasesmanager.client.datamodel;

import java.io.Serializable;

public class Parameter implements Serializable {

	private static final long serialVersionUID = 1L;
	private String value;
	private String name;
	private String paramDescription;
	private String objectType;
	private String defaultValue;

	public Parameter(String name, String paramDescription,
			String objectType, String defaultValue) {
		super();
		this.name=name;
		this.paramDescription=paramDescription;
		this.objectType=objectType;
		this.defaultValue=defaultValue;
	}

	public Parameter() {
		super();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParamDescription() {
		return paramDescription;
	}

	public void setParamDescription(String paramDescription) {
		this.paramDescription = paramDescription;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String toString() {
		return "Parameter [value=" + value + ", name=" + name
				+ ", paramDescription=" + paramDescription + ", objectType="
				+ objectType + ", defaultValue=" + defaultValue + "]";
	}
	
	
	
}
