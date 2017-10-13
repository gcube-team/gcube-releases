/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.parameters;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ListParameter extends Parameter {

	private static final long serialVersionUID = 5405965026753332225L;
	private String type;
	private String separator;

	/**
	 * 
	 */
	public ListParameter() {
		super();
		this.typology = ParameterType.LIST;
	}

	/**
	 * 
	 * @param name
	 *            name
	 * @param description
	 *            description
	 * @param type
	 *            type
	 * @param separator
	 *            separator
	 */
	public ListParameter(String name, String description, String type, String separator) {
		super(name, ParameterType.LIST, description);
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
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@Override
	public String toString() {
		return "ListParameter [type=" + type + ", value=" + value + ", separator=" + separator + ", name=" + name
				+ ", description=" + description + ", typology=" + typology + "]";
	}

}
