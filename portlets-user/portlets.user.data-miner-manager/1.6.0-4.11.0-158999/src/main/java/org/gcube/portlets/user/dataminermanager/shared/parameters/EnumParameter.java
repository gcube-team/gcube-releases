/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class EnumParameter extends Parameter {

	private static final long serialVersionUID = 1673874854501249519L;
	private List<String> values = new ArrayList<String>();
	private String defaultValue;

	/**
	 * 
	 */
	public EnumParameter() {
		super();
		this.typology = ParameterType.ENUM;
	}

	/**
	 * 
	 * @param name
	 *            name
	 * @param description
	 *            description
	 * @param values
	 *            list of value
	 * @param defaultValue
	 *            default value
	 */
	public EnumParameter(String name, String description, List<String> values, String defaultValue) {
		super(name, ParameterType.ENUM, description);
		this.values = values;
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

	/**
	 * @param values
	 *            the values to set
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

	@Override
	public String toString() {
		return "EnumParameter [values=" + values + ", defaultValue=" + defaultValue + ", value=" + value + ", name="
				+ name + ", description=" + description + ", typology=" + typology + "]";
	}

}
