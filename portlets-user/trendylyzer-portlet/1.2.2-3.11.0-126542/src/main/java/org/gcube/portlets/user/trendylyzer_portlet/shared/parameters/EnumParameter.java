/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.shared.parameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class EnumParameter extends Parameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1445332445910404248L;
	List<String> values = new ArrayList<String>();
	String defaultValue;
	String value;
	

	/**
	 * 
	 */
	public EnumParameter() {
//		super();
//		this.typology = ParameterTypology.ENUM;
	}
	
	/**
	 * @param type
	 * @param defaultValue
	 * @param value
	 */
	public EnumParameter(String name, String description, List<String> values, String defaultValue) {
		super(name, ParameterTypology.ENUM, description);
		this.values = values;
		this.defaultValue = defaultValue;
		this.typology = ParameterTypology.ENUM;
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
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}
	
	public void addValue(String value) {
		this.values.add(value);
	}
	
	/**
	 * @return the value
	 */
	@Override
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
