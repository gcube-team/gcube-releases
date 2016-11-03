/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.parameters;


/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DateParameter extends Parameter {

	private static final long serialVersionUID = 1673874854501249519L;
	private String defaultValue;
	private String value;

	/**
	 * 
	 */
	public DateParameter() {
		super();
		this.typology = ParameterType.DATE;
	}

	/**
	 * @param type
	 * @param defaultValue
	 * @param value
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

	/**
	 * @return the value
	 */
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "DateParameter [defaultValue=" + defaultValue + ", value="
				+ value + ", name=" + name + ", description=" + description
				+ ", typology=" + typology + "]";
	}

}
