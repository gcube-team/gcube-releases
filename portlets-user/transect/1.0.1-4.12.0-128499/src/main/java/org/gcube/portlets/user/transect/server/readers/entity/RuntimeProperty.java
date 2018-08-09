/**
 * 
 */
package org.gcube.portlets.user.transect.server.readers.entity;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 14, 2014
 *
 */
public class RuntimeProperty {
	

	private boolean isMandatory;
	private String name;
	private String value;
	
	
	/**
	 * @param key is parameter name
	 * @param defaultValue
	 * @param isMandatory
	 */
	public RuntimeProperty(String name, String value, boolean isMandatory) {
		super();
		this.name = name;
		this.value = value;
		this.isMandatory = isMandatory;
	}


	/**
	 * @return the isMandatory
	 */
	public boolean isMandatory() {
		return isMandatory;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}


	/**
	 * @param isMandatory the isMandatory to set
	 */
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RuntimeProperty [isMandatory=");
		builder.append(isMandatory);
		builder.append(", name=");
		builder.append(name);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}

	
}
