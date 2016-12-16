/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.shared.job;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Aug 1, 2014
 * 
 */
public class TdValidationDescription implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5682791497661793588L;

	private String description;
	private String validationColumn;
	private Integer conditionCode;

	/**
	 * 
	 */
	public TdValidationDescription() {
	}

	/**
	 * @param description
	 * @param validationColumn
	 * @param validationCode
	 */
	public TdValidationDescription(String description, String validationColumn,
			Integer conditionCode) {
		super();
		this.description = description;
		this.validationColumn = validationColumn;
		this.conditionCode = conditionCode;
	}

	public String getDescription() {
		return description;
	}

	public String getValidationColumn() {
		return validationColumn;
	}

	public Integer getConditionCode() {
		return conditionCode;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setValidationColumn(String validationColumn) {
		this.validationColumn = validationColumn;
	}

	public void setConditionCode(Integer conditionCode) {
		this.conditionCode = conditionCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdValidationDescription [description=");
		builder.append(description);
		builder.append(", validationColumn=");
		builder.append(validationColumn);
		builder.append(", conditionCode=");
		builder.append(conditionCode);
		builder.append("]");
		return builder.toString();
	}

}
