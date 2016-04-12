
package org.gcube.portlets.user.trendylyzer_portlet.shared.parameters;


import java.io.Serializable;


public class ObjectParameter extends Parameter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4464255715080152894L;
	private String type;
	private String defaultValue;
	private String value;
	
	
	/**
	 * 
	 */
	public ObjectParameter() {
//		super();
//		this.typology = ParameterTypology.OBJECT;
	}
	
	
	public ObjectParameter(String name, String description, String type, String defaultValue) {
		super(name, ParameterTypology.OBJECT, description);
		this.type = type;
		this.defaultValue = defaultValue;
		this.typology = ParameterTypology.OBJECT;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.bean.Parameter#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}

}
