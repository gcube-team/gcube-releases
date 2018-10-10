package org.gcube.application.framework.core.util;

/**
 * @author Valia Tsaqgkalidou (NKUA)
 */
public class Pair {
	protected String name;
	protected String value;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param name the name
	 * @param value the value
	 */
	public Pair(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	
}

