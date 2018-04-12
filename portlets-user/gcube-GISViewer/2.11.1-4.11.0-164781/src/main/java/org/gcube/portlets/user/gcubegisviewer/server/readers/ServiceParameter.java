/**
 * 
 */
package org.gcube.portlets.user.gcubegisviewer.server.readers;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 14, 2014
 *
 */
public class ServiceParameter {
	
	private String key;
	private String defaultValue;
	private boolean isMandatory;
	
	
	/**
	 * @param key is parameter name
	 * @param defaultValue
	 * @param isMandatory
	 */
	public ServiceParameter(String key, boolean isMandatory) {
		super();
		this.key = key;
		this.isMandatory = isMandatory;
	}
	
	
	public String getKey() {
		return key;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public boolean isMandatory() {
		return isMandatory;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceProperty [key=");
		builder.append(key);
		builder.append(", defaultValue=");
		builder.append(defaultValue);
		builder.append(", isMandatory=");
		builder.append(isMandatory);
		builder.append("]");
		return builder.toString();
	}
	
}
