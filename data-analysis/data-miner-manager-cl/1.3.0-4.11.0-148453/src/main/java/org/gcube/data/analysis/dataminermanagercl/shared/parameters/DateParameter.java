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
public class DateParameter extends Parameter {

	private static final long serialVersionUID = 1673874854501249519L;
	private String defaultValue;

	/**
	 * 
	 */
	public DateParameter() {
		super();
		this.typology = ParameterType.DATE;
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
	public DateParameter(String name, String description, String defaultValue) {
		super(name, ParameterType.DATE, description);
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
		return "DateParameter [defaultValue=" + defaultValue + ", value=" + value + ", name=" + name + ", description="
				+ description + ", typology=" + typology + "]";
	}

}
