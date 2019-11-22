/**
 * 
 */
package org.gcube.data.analysis.dataminermanagercl.shared.parameters;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ItemIdParameter extends Parameter {

	private static final long serialVersionUID = -8144663261336630929L;
	private String defaultValue;

	/**
	 * 
	 */
	public ItemIdParameter() {
		super();
		this.typology = ParameterType.ITEMID;
	}

	/**
	 * 
	 * @param name
	 *            name
	 * @param description
	 *            description
	 * @param defaultValue
	 *            default value
	 */
	public ItemIdParameter(String name, String description, String defaultValue) {
		super(name, ParameterType.ITEMID, description);
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return "ItemIdParameter [defaultValue=" + defaultValue + ", name=" + name + ", description=" + description
				+ ", typology=" + typology + ", value=" + value + "]";
	}

}
