package org.gcube.portlets.user.databasesmanager.client.datamodel;

import java.io.Serializable;

public class Parameter implements Serializable {

	private static final long serialVersionUID = 1L;
	private String value;
	private String name;
	private String paramDescription;
	private String objectType;
	private String defaultValue;

	public Parameter(String paramName, String paramDescription,
			String objectType, String defaultValue) {
		this.setName(paramName);
		this.setParamDescription(paramDescription);
		this.setObjectType(objectType);
		this.setDefaultValue(defaultValue);
	}

	public Parameter() {
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
}
