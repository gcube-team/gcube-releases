/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.shared.parameters;

import java.io.Serializable;


public class FileParameter extends Parameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7257886983774448854L;
	private String type;
	private String fileName;
	

	/**
	 * 
	 */
	public FileParameter() {
//		super();
//		this.typology = ParameterTypology.FILE;
	}
	

	public FileParameter(String name, String description) {
		super(name, ParameterTypology.FILE, description);
		this.typology = ParameterTypology.FILE;
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
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return getFileName();
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.bean.Parameter#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		this.setFileName(value);
	}

}
