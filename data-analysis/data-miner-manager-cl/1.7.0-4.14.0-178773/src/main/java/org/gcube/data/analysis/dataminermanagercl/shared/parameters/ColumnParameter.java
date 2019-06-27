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
public class ColumnParameter extends Parameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5084557326770554659L;
	private String referredTabularParameterName;
	private String defaultColumn;

	/**
	 * 
	 */
	public ColumnParameter() {
		super();
		this.typology = ParameterType.COLUMN;
	}

	/**
	 * 
	 * @param name
	 *            name
	 * @param description
	 *            description
	 * @param referredTabularParameterName
	 *            referred tabular parameter name
	 * @param defaultColumn
	 *            default column
	 */
	public ColumnParameter(String name, String description, String referredTabularParameterName, String defaultColumn) {
		super(name, ParameterType.COLUMN, description);
		this.referredTabularParameterName = referredTabularParameterName;
		this.defaultColumn = defaultColumn;
	}

	/**
	 * @param referredTabularParameterName
	 *            the referredTabularParameterName to set
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
	 * @return the defaultValue
	 */
	public String getDefaultColumn() {
		return defaultColumn;
	}

	/**
	 * 
	 * @param defaultColumn
	 *            default column
	 */
	public void setDefaultColumn(String defaultColumn) {
		this.defaultColumn = defaultColumn;
	}

	@Override
	public String toString() {
		return "ColumnParameter [referredTabularParameterName=" + referredTabularParameterName + ", defaultColumn="
				+ defaultColumn + ", value=" + value + ", name=" + name + ", description=" + description + ", typology="
				+ typology + "]";
	}

}
