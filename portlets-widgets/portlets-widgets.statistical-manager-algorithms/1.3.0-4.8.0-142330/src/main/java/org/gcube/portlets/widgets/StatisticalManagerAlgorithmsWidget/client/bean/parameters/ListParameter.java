/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author ceras
 *
 */
public class ListParameter extends Parameter implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2989796250868560500L;
	private String type;
	private String value;
	private String separator;

	/**
	 * 
	 */
	public ListParameter() {
		super();
		this.typology = ParameterTypology.LIST;
	}

	/**
	 * @param defaultValue
	 * @param value
	 */
	public ListParameter(String name, String description, String type, String separator) {
		super(name, ParameterTypology.LIST, description);
		this.type = type;
		this.separator = separator;
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
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.bean.Parameter#setValue()
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}
		
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.bean.Parameter#getValue()
	 */
	@Override
	public String getValue() {
		return value;
	}
	
	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}
}
