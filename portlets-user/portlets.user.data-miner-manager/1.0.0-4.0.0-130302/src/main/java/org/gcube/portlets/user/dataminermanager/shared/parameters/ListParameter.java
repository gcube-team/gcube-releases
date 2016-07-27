/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.parameters;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ListParameter extends Parameter implements IsSerializable {

	private static final long serialVersionUID = 5405965026753332225L;
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
	
	
	@Override
	public void setValue(String value) {
		this.value = value;
	}
		
	
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

	@Override
	public String toString() {
		return "ListParameter [type=" + type + ", value=" + value
				+ ", separator=" + separator + ", name=" + name
				+ ", description=" + description + ", typology=" + typology
				+ "]";
	}
	
	
	
}
