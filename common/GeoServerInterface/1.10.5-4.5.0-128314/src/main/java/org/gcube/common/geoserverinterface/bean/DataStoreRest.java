package org.gcube.common.geoserverinterface.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataStoreRest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3203832429579804847L;
	/**
	 * @uml.property  name="name"
	 */
	private String name = "";
	/**
	 * @uml.property  name="type"
	 */
	private String type = "";
	/**
	 * @uml.property  name="enabled"
	 */
	private boolean enabled = false;
	/**
	 * @uml.property  name="connectionParameters"
	 * @uml.associationEnd  qualifier="key:java.lang.String java.lang.String"
	 */
	private Map<String, String> connectionParameters = new HashMap<String, String>();
	
	public DataStoreRest() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 * @uml.property  name="type"
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 * @uml.property  name="type"
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return
	 * @uml.property  name="enabled"
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 * @uml.property  name="enabled"
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Map<String, String> getConnectionParameters() {
		return connectionParameters;
	}

	public void setConnectionParameters(Map<String, String> connectionParameters) {
		this.connectionParameters = connectionParameters;
	}
	
	public void setConnectionParameter(String key, String value) {
		this.connectionParameters.put(key, value);
	}
}
