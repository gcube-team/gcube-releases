/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.shared.parameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class ColumnListParameter extends Parameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6008181911327468923L;
	
	String referredTabularParameterName;
	List<String> columnNames = new ArrayList<String>();
	String value;
	private String separator;
	
	public ColumnListParameter() {
//		super();
//		this.typology = ParameterTypology.COLUMN_LIST;
	}
	
	public ColumnListParameter(String name, String description, String referredTabularParameterName, String separator) {
		super(name, ParameterTypology.COLUMN_LIST, description);
		this.referredTabularParameterName = referredTabularParameterName;
		this.separator = separator;
		this.typology = ParameterTypology.COLUMN_LIST;
	}

	/**
	 * @param referredTabularParameterName the referredTabularParameterName to set
	 */
	public void setReferredTabularParameterName(String referredTabularParameterName) {
		this.referredTabularParameterName = referredTabularParameterName;
	}
	
	/**
	 * @return the referredTabularParameterName
	 */
	public String getReferredTabularParameterName() {
		return referredTabularParameterName;
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
	
	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

}
