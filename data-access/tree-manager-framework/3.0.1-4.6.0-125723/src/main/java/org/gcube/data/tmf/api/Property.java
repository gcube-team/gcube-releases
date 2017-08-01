package org.gcube.data.tmf.api;

import java.io.Serializable;

/**
 * A plugin property.
 * 
 * @author Fabio Simeoni
 * 
 */
public class Property implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	private String value;

	public Property(String description, String name, String value) {
		this.name = name;
		this.description = description;
		this.value = value;
	}

	/**
	 * Returns the property name.
	 * 
	 * @return name
	 */
	public String name() {
		return name;
	}
	/**
	 * Return the property description.
	 * 
	 * @return description
	 */
	public String description() {
		return description;
	}

	/**
	 * Returns the property value.
	 * 
	 * @return value
	 */
	public String value() {
		return value;
	}

}
