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
public class TimeParameter extends Parameter {

	private static final long serialVersionUID = 1673874854501249519L;
	private String defaultValue;

	/**
	 * 
	 */
	public TimeParameter() {
		super();
		this.typology = ParameterType.TIME;
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
	public TimeParameter(String name, String description, String defaultValue) {
		super(name, ParameterType.TIME, description);
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue
	 *            the default value to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return "TimeParameter [defaultValue=" + defaultValue + ", value=" + value + ", name=" + name + ", description="
				+ description + ", typology=" + typology + "]";
	}

}
