/**
 * 
 */
package org.gcube.data.analysis.dataminermanagercl.shared.parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
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
	 * @param type
	 * @param defaultValue
	 * @param value
	 */
	public EnumParameter(String name, String description, List<String> values,
			String defaultValue) {
		super(name, ParameterType.ENUM, description);
		this.values = values;
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
		return "EnumParameter [values=" + values + ", defaultValue="
				+ defaultValue + ", value=" + value + ", name=" + name
				+ ", description=" + description + ", typology=" + typology
				+ "]";
	}

}
