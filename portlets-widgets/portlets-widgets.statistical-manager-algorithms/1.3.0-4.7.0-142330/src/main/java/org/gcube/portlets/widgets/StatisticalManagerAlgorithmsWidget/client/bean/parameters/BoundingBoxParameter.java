
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author ceras
 *
 */
public class BoundingBoxParameter extends Parameter implements IsSerializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5781578979785810377L;
	private String defaultValue;
	private String value;
	private String separator;
	
	
	public BoundingBoxParameter() {
		super();
		this.typology = ParameterTypology.BOUNDING_BOX;
	}
	
	
	public BoundingBoxParameter(String name, String description, String defaultValue, String separator) {
		super(name, ParameterTypology.BOUNDING_BOX, description);
		this.defaultValue = defaultValue;
		this.separator = separator;
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

	public String getSeparator() {
		return separator;
	}
}
